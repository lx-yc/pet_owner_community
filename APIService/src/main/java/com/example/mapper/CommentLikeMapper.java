package com.example.mapper;

import com.example.entity.CommentLike;
import com.example.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentLikeMapper {
    //增加评论点赞
    int insert(CommentLike commentLike);

    //取消评论点赞
    int deleteById(Long id);

    //查询评论点赞
    CommentLike selectByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    List<Long> selectLikedCommentIdsByUser(@Param("userId") Long userId,
                                           @Param("commentIds") List<Long> commentIds);
}

