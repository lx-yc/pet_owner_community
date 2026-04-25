package com.example.service;

import com.example.dto.post.PostCursorQuery;
import com.example.dto.post.PostDTO;
import com.example.vo.page.CursorPageVO;
import com.example.vo.post.PostVO;

import java.util.Map;

public interface PostService {
    //根据帖子id
    PostVO getPostById(Long postId);
    // 新增帖子
    void insertPost(PostDTO postDTO);
    // 获取帖子列表
    CursorPageVO<PostVO> getPostList(PostCursorQuery query);
    // 更新帖子
    void updatePost(PostDTO postDTO);
    // 删除帖子
    void deletePost(Long postId);
    // 点赞
    boolean addOrCancelLike(Long postId);
    // 收藏
    boolean addOrCancelFavorite(Long postId);
    // 浏览
    void increaseViewCount(Long postId);
    // 获取用户点赞的帖子列表
    CursorPageVO<PostVO> getUserLikedPosts(Long userId, Long lastId, Integer pageSize);
    // 获取用户收藏的帖子列表
    CursorPageVO<PostVO> getUserFavoritedPosts(Long userId, Long lastId, Integer pageSize);
    // 获取用户统计数据（作品数/点赞数/收藏数）
    Map<String, Object> getUserPostStats(Long userId);
}
