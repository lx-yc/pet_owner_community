package com.example.mapper;

import com.example.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostLikeMapper {
    //增加点赞
    int insert(PostLike postLike);

    //取消点赞
    int deleteById(@Param("id") Long id);

    //查询点赞
    PostLike selectByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);


    List<Long> selectLikedPostIdsByUser(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    // 获取用户点赞的帖子ID列表（游标分页）
    List<Long> selectLikedPostIdsByUserId(@Param("userId") Long userId, @Param("lastId") Long lastId, @Param("pageSize") Integer pageSize);
}
