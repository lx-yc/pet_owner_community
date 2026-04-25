package com.example.mapper;

import com.example.entity.Comment;
import com.example.vo.Comment.CommentVO;
import com.example.vo.Comment.ReplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    //新增评论
    int insertComment(Comment comment);

    //删除评论
    int deleteById(Long id);

    //更新评论
    int updateComment(Comment comment);

    int getReplyCount(@Param("commentId") Long commentId, @Param("postId") Long postId);

    // 游标分页查询一级评论
    List<CommentVO> selectTopCommentByCursor(
            @Param("postId") Long postId,
            @Param("lastId") Long lastId,
            @Param("pageSize") Integer pageSize
    );

    // 游标分页查询楼中楼回复
    List<ReplyVO> selectReplyByCursor(
            @Param("parentId") Long parentId,
            @Param("lastId") Long lastId,
            @Param("pageSize") Integer pageSize
    );

    // 获取评论
    Comment selectById(Long id);

    //评论的点赞数+1
    int increaseLikeCount(Long commentId);

    //评论的点赞数-1
    int decreaseLikeCount(Long commentId);

}
