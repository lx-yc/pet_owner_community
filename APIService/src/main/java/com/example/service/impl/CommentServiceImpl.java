package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.example.dto.comment.CommentCursorQuery;
import com.example.dto.comment.CommentDTO;
import com.example.dto.comment.CommentReplyCursorQuery;
import com.example.dto.notification.NotificationMessageDTO;
import com.example.entity.Comment;
import com.example.entity.CommentLike;
import com.example.entity.Post;
import com.example.enums.NotificationType;
import com.example.enums.ResultCode;
import com.example.mapper.CommentLikeMapper;
import com.example.mapper.CommentMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.UserMapper;
import com.example.rabbitMQ.NoticeProducer;
import com.example.service.CommentService;
import com.example.utils.BeanConvertUtils;
import com.example.utils.GlobalCheckUtil;
import com.example.utils.UserContext;
import com.example.vo.Comment.CommentVO;
import com.example.vo.Comment.CursorIdAware;
import com.example.vo.Comment.ReplyVO;
import com.example.vo.page.CursorPageVO;
import com.example.vo.user.UserPostListVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private NoticeProducer noticeProducer;

    @Resource
    private UserMapper userMapper;

    @Override
    public void insertComment(CommentDTO commentDTO) {
        Long userId = GlobalCheckUtil.checkLogin();
        Post post = postMapper.selectById(commentDTO.getPostId());
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);
        commentDTO.setUserId(userId);
        if (post.getUserId() == userId) {
            commentDTO.setIsAuthor(1);
        } else {
            commentDTO.setIsAuthor(0);
        }
        Comment comment = BeanConvertUtils.convert(commentDTO, Comment.class);
        GlobalCheckUtil.checkRowAffect(commentMapper.insertComment(comment), ResultCode.COMMENT_INSERT_FAILED);
        GlobalCheckUtil.checkRowAffect(postMapper.increaseCommentCount(comment.getPostId()), ResultCode.POST_COMMENT_COUNT_INCREASE_FAILED);

        // ============== 发送通知消息 ==============
        // ---- 场景1：发给【帖子作者】的通知 ----
        if (!post.getUserId().equals(userId)) {
            NotificationMessageDTO dto = new NotificationMessageDTO();
            dto.setUserId(post.getUserId());
            dto.setFromUserId(userId);
            dto.setType(NotificationType.POST_COMMENT.getCode());
            dto.setPostId(comment.getPostId());
            dto.setCommentId(comment.getId());
            dto.setContent(NotificationType.buildContent(dto.getType(), userId)+" : "+comment.getContent());
            noticeProducer.send(dto);
        }

        // ---- 场景2：发给【被回复的人】的通知 ----
        // 判断：有父ID（说明是回复），且父ID对应的用户不是自己，才发
        if (commentDTO.getParentId() != null && commentDTO.getParentId() > 0) {
            // 根据父ID查询被回复的那条评论
            Comment parentComment = commentMapper.selectById(commentDTO.getParentId());

            // 防御性检查：评论存在
            if (parentComment != null) {
                Long toUserId = parentComment.getUserId(); // 被回复人的ID

                // 关键判断：不给自己发通知
                if (!toUserId.equals(userId)) {
                    NotificationMessageDTO replyDto = new NotificationMessageDTO();
                    replyDto.setUserId(toUserId);                // 接收人 = 被回复人
                    replyDto.setFromUserId(userId);              // 发送人 = 当前回复人
                    replyDto.setType(NotificationType.REPLY_COMMENT.getCode()); // 类型：回复了你的评论
                    replyDto.setPostId(comment.getPostId());
                    replyDto.setCommentId(comment.getId());       // 刚生成的评论ID
                    replyDto.setContent(NotificationType.buildContent(replyDto.getType(), userId));
                    noticeProducer.send(replyDto);
                }
            }
        }
    }

    @Override
    public void updateComment(CommentDTO commentDTO) {
        Long userId = GlobalCheckUtil.checkLogin();
        Comment comment = commentMapper.selectById(commentDTO.getId());
        GlobalCheckUtil.checkNotNull(comment, ResultCode.COMMENT_NOT_EXIST);
        GlobalCheckUtil.checkOwner(comment.getUserId(), userId, ResultCode.COMMENT_NO_PERMISSION);

        commentDTO.setUserId(userId);
        int rows = commentMapper.updateComment(BeanConvertUtils.convert(commentDTO, Comment.class));
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.COMMENT_UPDATE_FAILED);
    }

    @Override
    public void deleteById(Long id) {
        Long userId = GlobalCheckUtil.checkLogin();
        Comment comment = commentMapper.selectById(id);
        GlobalCheckUtil.checkNotNull(comment, ResultCode.COMMENT_NOT_EXIST);
        GlobalCheckUtil.checkOwner(comment.getUserId(), userId, ResultCode.COMMENT_NO_PERMISSION);
        GlobalCheckUtil.checkRowAffect(commentMapper.deleteById(id), ResultCode.COMMENT_DELETE_FAILED);
        GlobalCheckUtil.checkRowAffect(postMapper.decreaseCommentCount(comment.getPostId()), ResultCode.POST_COMMENT_COUNT_DECREASE_FAILED);
    }

    @Override
    public boolean addOrCancelLike(Long commentId) {
        Long userId = GlobalCheckUtil.checkLogin();
        Comment comment = commentMapper.selectById(commentId);
        GlobalCheckUtil.checkNotNull(comment, ResultCode.COMMENT_NOT_EXIST);
        CommentLike like = commentLikeMapper.selectByUserIdAndCommentId(userId, commentId);
        if (like == null) {
            //给评论点赞
            CommentLike newCommentLike = new CommentLike();
            newCommentLike.setUserId(userId);
            newCommentLike.setCommentId(commentId);
            GlobalCheckUtil.checkRowAffect(commentLikeMapper.insert(newCommentLike), ResultCode.COMMENT_LIKE_ADD_FAILED);
            //评论点赞数+1
            GlobalCheckUtil.checkRowAffect(commentMapper.increaseLikeCount(commentId), ResultCode.COMMENT_LIKE_ADD_FAILED);

            // ============== 发送通知消息 ==============

            // 组装DTO
            if (!comment.getUserId().equals(userId)){
                NotificationMessageDTO dto = new NotificationMessageDTO();
                dto.setUserId(comment.getUserId());
                dto.setFromUserId(userId);
                dto.setType(NotificationType.COMMENT_LIKE.getCode()); // 你自己的类型
                dto.setPostId(comment.getPostId());
                dto.setCommentId(comment.getId());
                dto.setContent(NotificationType.buildContent(dto.getType(), userId));
                // 发送到RabbitMQ
                noticeProducer.send(dto);
            }

            return true;

        } else {
            //取消点赞
            GlobalCheckUtil.checkRowAffect(commentLikeMapper.deleteById(like.getId()), ResultCode.COMMENT_LIKE_CANCEL_FAILED);
            //评论点赞数-1
            GlobalCheckUtil.checkRowAffect(commentMapper.decreaseLikeCount(commentId), ResultCode.COMMENT_LIKE_CANCEL_FAILED);

            return false;
        }
    }

    @Override
    public CursorPageVO<CommentVO> getTopCommentPage(CommentCursorQuery query) {
        Post post = postMapper.selectById(query.getPostId());
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);

        List<CommentVO> list = commentMapper.selectTopCommentByCursor(
                query.getPostId(),
                query.getLastId(),
                query.getPageSize() + 1
        );

        // ===================== 【和你帖子逻辑完全一样】的评论点赞判断 =====================
        if (CollUtil.isNotEmpty(list)) {
            Long userId = UserContext.getUserId();
            // 提取评论ID
            List<Long> commentIds = list.stream()
                    .map(CommentVO::getId)
                    .collect(Collectors.toList());

            // 查询当前用户点赞过的评论ID（转成 HashSet 提高判断速度）
            Set<Long> likedCommentSet = new HashSet<>();
            if (userId != null) {
                likedCommentSet = new HashSet<>(commentLikeMapper.selectLikedCommentIdsByUser(userId, commentIds));
            }

            // 赋值：用户信息 + 是否已点赞
            for (CommentVO commentVO : list) {
                UserPostListVO userPostListVO = userMapper.selectUserPostById(commentVO.getUserId());
                commentVO.setAvatar(userPostListVO.getAvatar());
                commentVO.setNickname(userPostListVO.getNickname());

                Integer replyCount = commentMapper.getReplyCount(commentVO.getId(), commentVO.getPostId());
                commentVO.setReplyCount(replyCount);
                // 赋值点赞状态（和帖子的 isLiked 写法完全一样）
                commentVO.setIsLiked(likedCommentSet.contains(commentVO.getId()));
            }
        }
        return buildCursorPage(list, query.getPageSize());
    }

    @Override
    public CursorPageVO<ReplyVO> getReplyPage(CommentReplyCursorQuery query) {
        Comment parentComment = commentMapper.selectById(query.getParentId());
        GlobalCheckUtil.checkNotNull(parentComment, ResultCode.COMMENT_NOT_EXIST);
        List<ReplyVO> list = commentMapper.selectReplyByCursor(
                query.getParentId(),
                query.getLastId(),
                query.getPageSize() + 1
        );

            for (ReplyVO replyVO : list) {
                UserPostListVO userPostListVO = userMapper.selectUserPostById(replyVO.getUserId());
                replyVO.setAvatar(userPostListVO.getAvatar());
                replyVO.setNickname(userPostListVO.getNickname());
                UserPostListVO replyUserPostListVO = userMapper.selectUserPostById(replyVO.getReplyUserId());
                replyVO.setReplyAvatar(replyUserPostListVO.getAvatar());
                replyVO.setReplyNickname(replyUserPostListVO.getNickname());
            }

        return buildCursorPage(list, query.getPageSize());
    }

    private <T extends CursorIdAware> CursorPageVO<T> buildCursorPage(List<T> list, Integer pageSize) {
        CursorPageVO<T> vo = new CursorPageVO<>();
        boolean isLast = list.size() <= pageSize;

        // 不是最后一页，移除多查的一条
        if (!isLast) {
            list.remove(list.size() - 1);
        }

        vo.setList(list);
        vo.setIsLast(isLast);

        // 设置下一页游标ID
        if (!isLast && !list.isEmpty()) {
            // 这里需要你的VO有getId()方法，必须加！
            T last = list.get(list.size() - 1);
            vo.setNextCursorId(last.getId());
        }
        return vo;
    }
}
