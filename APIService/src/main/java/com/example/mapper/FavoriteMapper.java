package com.example.mapper;

import com.example.entity.Favorite;
import com.example.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    //增加收藏
    int insert(Favorite favorite);

    //取消收藏
    int deleteById(@Param("id") Long id);

    //查询收藏
    Favorite selectByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    List<Long> selectCollectedPostIdsByUser(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    // 获取用户收藏的帖子ID列表（游标分页）
    List<Long> selectCollectedPostIdsByUserId(@Param("userId") Long userId, @Param("lastId") Long lastId, @Param("pageSize") Integer pageSize);

    // 获取用户所有收藏的帖子ID列表（用于登录时加载到Redis）
    List<Long> selectAllCollectedPostIdsByUserId(@Param("userId") Long userId);
}
