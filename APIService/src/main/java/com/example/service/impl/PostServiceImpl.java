package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.example.dto.notification.NotificationMessageDTO;
import com.example.dto.post.PostCursorQuery;
import com.example.dto.post.PostDTO;
import com.example.entity.Favorite;
import com.example.entity.Notification;
import com.example.entity.Post;
import com.example.entity.PostLike;
import com.example.enums.NotificationType;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.mapper.FavoriteMapper;
import com.example.mapper.PostLikeMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.UserMapper;
import com.example.rabbitMQ.NoticeProducer;
import com.example.service.PostService;
import com.example.service.UploadService;
import com.example.utils.BeanConvertUtils;
import com.example.utils.GlobalCheckUtil;
import com.example.utils.SeasonUtils;
import com.example.utils.UserContext;
import com.example.vo.page.CursorPageVO;
import com.example.vo.post.PostVO;
import com.example.vo.user.UserLoginVO;
import com.example.vo.user.UserPostListVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    @Resource
    private PostMapper postMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private NoticeProducer noticeProducer;

    @Resource
    private UploadService uploadService;

    @Override
    public PostVO getPostById(Long postId) {
        Post post = postMapper.selectById(postId);
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);
        PostVO postVO = BeanConvertUtils.convert(post, PostVO.class);
        // 给每个帖子添加作者的头像和昵称
        Long postUserId = postVO.getUserId();
        UserPostListVO userPostListVO = userMapper.selectUserPostById(postUserId);
        postVO.setUserNickname(userPostListVO.getNickname());
        postVO.setUserAvatar(userPostListVO.getAvatar());

        Long userId = UserContext.getUserId();
        if (userId != null) {
            postVO.setIsLiked(postLikeMapper.selectByUserIdAndPostId(userId, postId) != null);
            postVO.setIsCollected(favoriteMapper.selectByUserIdAndPostId(userId, postId) != null);
        }else {
            postVO.setIsLiked(false);
            postVO.setIsCollected(false);
        }
        return postVO;
    }

    @Override
    public void insertPost(@RequestBody PostDTO postDTO) {

        Long userId = GlobalCheckUtil.checkLogin();
        UserLoginVO userLoginVO = userMapper.selectById(userId);
        GlobalCheckUtil.checkNotNull(userLoginVO, ResultCode.USER_NOT_EXIST);

        postDTO.setUserId(userId);
        Post post = BeanConvertUtils.convert(postDTO, Post.class);
        post.setIsOfficial(userLoginVO.getRole().equals("admin") ? 1 : 0);
        if (post.getIsOfficial() != 1 && post.getSeason() != null && !post.getSeason().isEmpty()) {
            throw new BusinessException(ResultCode.USER_NO_PERMISSION);
        }
        int rows = postMapper.insertPost(post);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.POST_INSERT_FAILED);
    }

    @Override
    public CursorPageVO<PostVO> getPostList(@RequestParam PostCursorQuery query) {
        Long userId = UserContext.getUserId();
        String season = SeasonUtils.getCurrentSeason();
        query.setSeason(season);

        if (query.getIsMine() == 1) {
            query.setUserId(userId);
        }
        
        int pageSize = query.getPageSize();
        query.setPageSize(pageSize + 1);

        List<PostVO> list = postMapper.getAllPosts(query);

        if (list != null && !list.isEmpty()) {
            if (userId != null) {
                List<Long> postIds = list.stream()
                        .map(PostVO::getId)
                        .collect(Collectors.toList());

                Set<Long> likedSet = new HashSet<>(postLikeMapper.selectLikedPostIdsByUser(userId, postIds));
                Set<Long> collectedSet = new HashSet<>(favoriteMapper.selectCollectedPostIdsByUser(userId, postIds));

                for (PostVO vo : list) {
                    vo.setIsLiked(likedSet.contains(vo.getId()));
                    vo.setIsCollected(collectedSet.contains(vo.getId()));
                }
            } else {
                for (PostVO vo : list) {
                    vo.setIsLiked(false);
                    vo.setIsCollected(false);
                }
            }
        }

        for (PostVO postVO : list) {
            Long postUserId = postVO.getUserId();
            UserPostListVO userPostListVO = userMapper.selectUserPostById(postUserId);
            postVO.setUserNickname(userPostListVO.getNickname());
            postVO.setUserAvatar(userPostListVO.getAvatar());
        }
        
        boolean isLast = list.size() <= pageSize;

        CursorPageVO<PostVO> result = new CursorPageVO<>();
        result.setIsLast(isLast);

        if (!list.isEmpty()) {
            if (!isLast) {
                list.remove(list.size() - 1);
            }
            PostVO last = list.get(list.size() - 1);
            result.setNextCursorId(last.getId());
            result.setNextCursorTime(LocalDateTimeUtil.format(last.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            
            if ("official".equals(query.getSortType())) {
                Integer seasonPriority = (last.getSeason() != null && last.getSeason().equals(season)) ? 0 : 1;
                result.setNextCursorSeasonPriority(seasonPriority);
            }
        }

        result.setList(list);
        return result;
    }

    @Override
    public void updatePost(@RequestBody PostDTO postDTO) {
        Long userId = GlobalCheckUtil.checkLogin();
        Post post = postMapper.selectById(postDTO.getId());
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);
        GlobalCheckUtil.checkOwner(post.getUserId(), userId, ResultCode.POST_NO_PERMISSION);

        postDTO.setUserId(userId);
        int rows = postMapper.updatePost(BeanConvertUtils.convert(postDTO, Post.class));
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.POST_UPDATE_FAILED);
    }

    @Override
    public void deletePost(@RequestParam Long postId) {
        Long userId = GlobalCheckUtil.checkLogin();
        Post post = postMapper.selectById(postId);
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);
        GlobalCheckUtil.checkOwner(post.getUserId(), userId, ResultCode.POST_NO_PERMISSION);

        if (post.getImages() != null && !post.getImages().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> imageUrls = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                
                for (String imageUrl : imageUrls) {
                    try {
                        uploadService.deleteImage(imageUrl);
                    } catch (Exception e) {
                        System.err.println("删除OSS图片失败: " + imageUrl + ", 错误: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("解析帖子图片列表失败: " + e.getMessage());
            }
        }

        int rows = postMapper.deleteById(postId);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.POST_DELETE_FAILED);

    }

    @Override
    public boolean addOrCancelLike(@RequestParam Long postId) {
        Long userId = GlobalCheckUtil.checkLogin();
        Post post = postMapper.selectById(postId);
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);

        PostLike postLike = postLikeMapper.selectByUserIdAndPostId(userId, postId);
        if (postLike != null) {
            //取消点赞
            int deleteRows = postLikeMapper.deleteById(postLike.getId());
            GlobalCheckUtil.checkRowAffect(deleteRows, ResultCode.POST_LIKE_CANCEL_FAILED);
            //帖子点赞数-1
            int updateRows = postMapper.decreaseLikeCount(postId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.POST_LIKE_CANCEL_FAILED);

            //发布该帖子的用户获赞数-1
            int updateUserRows = userMapper.decreaseLikeCount(post.getUserId());
            GlobalCheckUtil.checkRowAffect(updateUserRows, ResultCode.POST_LIKE_CANCEL_FAILED);
            return false;
        } else {
            //点赞
            PostLike newpostLike = new PostLike();
            newpostLike.setUserId(userId);
            newpostLike.setPostId(postId);
            int insertRows = postLikeMapper.insert(newpostLike);
            GlobalCheckUtil.checkRowAffect(insertRows, ResultCode.POST_LIKE_ADD_FAILED);
            //帖子点赞数+1
            int updateRows = postMapper.increaseLikeCount(postId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.POST_LIKE_ADD_FAILED);
            //发布该帖子的用户获赞数+1
            int updateUserRows = userMapper.increaseLikeCount(post.getUserId());
            GlobalCheckUtil.checkRowAffect(updateUserRows, ResultCode.POST_LIKE_ADD_FAILED);

            // ============== 发送通知消息 ==============
            if (!post.getUserId().equals(userId)) {
                NotificationMessageDTO dto = new NotificationMessageDTO();
                dto.setUserId(post.getUserId());
                dto.setFromUserId(userId);
                dto.setType(NotificationType.POST_LIKE.getCode());
                dto.setPostId(postId);
                dto.setContent(NotificationType.buildContent(dto.getType(), userId));
                noticeProducer.send(dto);
            }

            return true;
        }
    }

    @Override
    public boolean addOrCancelFavorite(@RequestParam Long postId) {
        Long userId = GlobalCheckUtil.checkLogin();
        Post post = postMapper.selectById(postId);
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);

        Favorite favorite = favoriteMapper.selectByUserIdAndPostId(userId, postId);
        if (favorite != null) {
            //取消收藏
            int deleteRows = favoriteMapper.deleteById(favorite.getId());
            GlobalCheckUtil.checkRowAffect(deleteRows, ResultCode.POST_FAVORITE_CANCEL_FAILED);

            //收藏数-1
            int updateRows = postMapper.decreaseCollectCount(postId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.POST_FAVORITE_CANCEL_FAILED);

            return false;
        } else {
            //收藏
            Favorite newfavorite = new Favorite();
            newfavorite.setUserId(userId);
            newfavorite.setPostId(postId);
            int insertRows = favoriteMapper.insert(newfavorite);
            GlobalCheckUtil.checkRowAffect(insertRows, ResultCode.POST_FAVORITE_ADD_FAILED);


            int updateRows = postMapper.increaseCollectCount(postId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.POST_FAVORITE_ADD_FAILED);

            // ============== 发送通知消息 ==============
            if (!post.getUserId().equals(userId)) {
                NotificationMessageDTO dto = new NotificationMessageDTO();
                dto.setUserId(post.getUserId());
                dto.setFromUserId(userId);
                dto.setType(NotificationType.COLLECT.getCode());
                dto.setPostId(postId);
                dto.setContent(NotificationType.buildContent(dto.getType(), userId));
                noticeProducer.send(dto);
            }

            return true;
        }
    }

    @Override
    public void increaseViewCount(@RequestParam Long postId) {
        Post post = postMapper.selectById(postId);
        GlobalCheckUtil.checkNotNull(post, ResultCode.POST_NOT_EXIST);

        int updateRows = postMapper.increaseViewCount(postId);
        GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.POST_VIEW_COUNT_INCREASE_FAILED);
    }

    @Override
    public CursorPageVO<PostVO> getUserLikedPosts(Long userId, Long lastId, Integer pageSize) {
        int actualPageSize = pageSize + 1;
        List<Long> likedPostIds = postLikeMapper.selectLikedPostIdsByUserId(userId, lastId, actualPageSize);

        if (likedPostIds == null || likedPostIds.isEmpty()) {
            CursorPageVO<PostVO> result = new CursorPageVO<>();
            result.setIsLast(true);
            result.setList(new ArrayList<>());
            return result;
        }

        boolean isLast = likedPostIds.size() <= pageSize;
        if (!isLast) {
            likedPostIds.remove(likedPostIds.size() - 1);
        }

        List<PostVO> list = postMapper.selectByIds(likedPostIds);

        Long currentUserId = UserContext.getUserId();
        if (list != null && !list.isEmpty() && currentUserId != null) {
            List<Long> postIds = list.stream().map(PostVO::getId).collect(Collectors.toList());
            Set<Long> likedSet = new HashSet<>(postLikeMapper.selectLikedPostIdsByUser(currentUserId, postIds));
            Set<Long> collectedSet = new HashSet<>(favoriteMapper.selectCollectedPostIdsByUser(currentUserId, postIds));
            for (PostVO vo : list) {
                vo.setIsLiked(likedSet.contains(vo.getId()));
                vo.setIsCollected(collectedSet.contains(vo.getId()));
            }
        } else if (list != null && !list.isEmpty()) {
            for (PostVO vo : list) {
                vo.setIsLiked(false);
                vo.setIsCollected(false);
            }
        }

        for (PostVO postVO : list) {
            Long postUserId = postVO.getUserId();
            UserPostListVO userPostListVO = userMapper.selectUserPostById(postUserId);
            postVO.setUserNickname(userPostListVO.getNickname());
            postVO.setUserAvatar(userPostListVO.getAvatar());
        }

        CursorPageVO<PostVO> result = new CursorPageVO<>();
        result.setIsLast(isLast);
        if (!list.isEmpty()) {
            PostVO last = list.get(list.size() - 1);
            result.setNextCursorId(last.getId());
            result.setNextCursorTime(LocalDateTimeUtil.format(last.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        result.setList(list);
        return result;
    }

    @Override
    public CursorPageVO<PostVO> getUserFavoritedPosts(Long userId, Long lastId, Integer pageSize) {
        int actualPageSize = pageSize + 1;
        List<Long> favoritedPostIds = favoriteMapper.selectCollectedPostIdsByUserId(userId, lastId, actualPageSize);

        if (favoritedPostIds == null || favoritedPostIds.isEmpty()) {
            CursorPageVO<PostVO> result = new CursorPageVO<>();
            result.setIsLast(true);
            result.setList(new ArrayList<>());
            return result;
        }

        boolean isLast = favoritedPostIds.size() <= pageSize;
        if (!isLast) {
            favoritedPostIds.remove(favoritedPostIds.size() - 1);
        }

        List<PostVO> list = postMapper.selectByIds(favoritedPostIds);

        Long currentUserId = UserContext.getUserId();
        if (list != null && !list.isEmpty() && currentUserId != null) {
            List<Long> postIds = list.stream().map(PostVO::getId).collect(Collectors.toList());
            Set<Long> likedSet = new HashSet<>(postLikeMapper.selectLikedPostIdsByUser(currentUserId, postIds));
            Set<Long> collectedSet = new HashSet<>(favoriteMapper.selectCollectedPostIdsByUser(currentUserId, postIds));
            for (PostVO vo : list) {
                vo.setIsLiked(likedSet.contains(vo.getId()));
                vo.setIsCollected(collectedSet.contains(vo.getId()));
            }
        } else if (list != null && !list.isEmpty()) {
            for (PostVO vo : list) {
                vo.setIsLiked(false);
                vo.setIsCollected(false);
            }
        }

        for (PostVO postVO : list) {
            Long postUserId = postVO.getUserId();
            UserPostListVO userPostListVO = userMapper.selectUserPostById(postUserId);
            postVO.setUserNickname(userPostListVO.getNickname());
            postVO.setUserAvatar(userPostListVO.getAvatar());
        }

        CursorPageVO<PostVO> result = new CursorPageVO<>();
        result.setIsLast(isLast);
        if (!list.isEmpty()) {
            PostVO last = list.get(list.size() - 1);
            result.setNextCursorId(last.getId());
            result.setNextCursorTime(LocalDateTimeUtil.format(last.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        result.setList(list);
        return result;
    }

    @Override
    public Map<String, Object> getUserPostStats(Long userId) {
        int postCount = postMapper.countPostsByUserId(userId);
        int likeCount = postMapper.countLikedPostsByUserId(userId);
        int favoriteCount = postMapper.countFavoritedPostsByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("postCount", postCount);
        result.put("likeCount", likeCount);
        result.put("favoriteCount", favoriteCount);
        return result;
    }
}
