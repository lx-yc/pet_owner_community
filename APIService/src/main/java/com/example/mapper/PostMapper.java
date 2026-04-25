package com.example.mapper;

import com.example.dto.post.PostCursorQuery;
import com.example.dto.post.PostDTO;
import com.example.entity.Post;
import com.example.entity.PostLike;
import com.example.vo.post.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    //新增帖子
    int insertPost(Post post);

    //查询所有帖子
    List<PostVO> getAllPosts(@Param("query") PostCursorQuery query);

    //更新帖子
    int updatePost(@Param("post") Post post);

    //删除帖子
    int deleteById(@Param("id") Long id);

    //根据id查询帖子信息
    Post selectById(Long id);

    //点赞数+1
    int increaseLikeCount(@Param("postId") Long postId);

    //取消点赞数-1
    int decreaseLikeCount(@Param("postId") Long postId);

    //收藏数+1
    int increaseCollectCount(@Param("postId") Long postId);

    //取消收藏数-1
    int decreaseCollectCount(@Param("postId") Long postId);

    //浏览量+1
    int increaseViewCount(@Param("postId") Long postId);

    //评论数+1
    int increaseCommentCount(@Param("postId") Long postId);

    //评论数-1
    int decreaseCommentCount(@Param("postId") Long postId);

    //根据帖子ID列表批量查询帖子
    List<PostVO> selectByIds(@Param("postIds") List<Long> postIds);

    //统计指定用户的帖子总数
    int countPostsByUserId(@Param("userId") Long userId);

    //统计指定用户点赞的帖子总数
    int countLikedPostsByUserId(@Param("userId") Long userId);

    //统计指定用户收藏的帖子总数
    int countFavoritedPostsByUserId(@Param("userId") Long userId);
}
