package com.example.controller;

import com.example.dto.post.PostCursorQuery;
import com.example.dto.post.PostDTO;
import com.example.dto.post.PostIdDTO;
import com.example.service.PostService;
import com.example.utils.BeanConvertUtils;
import com.example.utils.Result;
import com.example.vo.page.CursorPageVO;
import com.example.vo.post.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "帖子管理", description = "帖子的增删改查、点赞、收藏等接口")
@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Operation(summary = "获取帖子详情", description = "根据ID获取帖子详情")
    @GetMapping("/detail")
    public Result<PostVO> detail(@RequestParam Long id) {
        PostVO postVO = postService.getPostById(id);
        return Result.success(postVO, "获取帖子详情成功");
    }

    @Operation(summary = "发布帖子", description = "创建新的帖子")
    @PostMapping("/insert")
    public Result<Integer> insert(@RequestBody PostDTO postDTO) {
        postService.insertPost(postDTO);
        return Result.success("新增帖子成功");
    }

    @Operation(summary = "更新帖子", description = "更新已有帖子的内容")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody PostDTO postDTO) {
        postService.updatePost(postDTO);
        return Result.success("更新帖子成功");
    }

    @Operation(summary = "获取帖子列表", description = "分页获取帖子列表（游标分页）")
    @GetMapping("/list")
    public Result<CursorPageVO<PostVO>> list(PostCursorQuery query) {
        query.setIsMine(0);
        return Result.success(postService.getPostList(query), "获取帖子列表成功");
    }

    @Operation(summary = "获取我的帖子列表", description = "分页获取当前用户发布的帖子列表")
    @GetMapping("/my/list")
    public Result<CursorPageVO<PostVO>> mylist(PostCursorQuery query) {
        query.setIsMine(1);
        return Result.success(postService.getPostList(query), "获取个人帖子成功");
    }

    @Operation(summary = "获取我的点赞列表", description = "分页获取当前用户点赞的帖子列表")
    @GetMapping("/my/likes")
    public Result<CursorPageVO<PostVO>> myLikes(@RequestParam(required = false) Long lastId,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = com.example.utils.UserContext.getUserId();
        return Result.success(postService.getUserLikedPosts(userId, lastId, pageSize), "获取我的点赞列表成功");
    }

    @Operation(summary = "获取我的收藏列表", description = "分页获取当前用户收藏的帖子列表")
    @GetMapping("/my/favorites")
    public Result<CursorPageVO<PostVO>> myFavorites(@RequestParam(required = false) Long lastId,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = com.example.utils.UserContext.getUserId();
        return Result.success(postService.getUserFavoritedPosts(userId, lastId, pageSize), "获取我的收藏列表成功");
    }

    @Operation(summary = "获取指定用户的帖子列表", description = "分页获取指定用户发布的帖子列表")
    @GetMapping("/user/{userId}/posts")
    public Result<CursorPageVO<PostVO>> getUserPosts(@PathVariable Long userId, PostCursorQuery query) {
        query.setUserId(userId);
        query.setIsMine(0);
        return Result.success(postService.getPostList(query), "获取用户帖子成功");
    }

    @Operation(summary = "获取指定用户的点赞列表", description = "分页获取指定用户点赞的帖子列表")
    @GetMapping("/user/{userId}/likes")
    public Result<CursorPageVO<PostVO>> getUserLikes(@PathVariable Long userId,
                                                      @RequestParam(required = false) Long lastId,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.getUserLikedPosts(userId, lastId, pageSize), "获取用户点赞列表成功");
    }

    @Operation(summary = "获取指定用户的收藏列表", description = "分页获取指定用户收藏的帖子列表")
    @GetMapping("/user/{userId}/favorites")
    public Result<CursorPageVO<PostVO>> getUserFavorites(@PathVariable Long userId,
                                                          @RequestParam(required = false) Long lastId,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.getUserFavoritedPosts(userId, lastId, pageSize), "获取用户收藏列表成功");
    }

    @Operation(summary = "获取用户帖子统计数据", description = "获取指定用户的作品数、点赞数、收藏数")
    @GetMapping("/user/{userId}/stats")
    public Result<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        return Result.success(postService.getUserPostStats(userId), "获取用户统计数据成功");
    }

    @Operation(summary = "获取我的帖子统计数据", description = "获取当前用户的作品数、点赞数、收藏数")
    @GetMapping("/my/stats")
    public Result<Map<String, Object>> getMyStats() {
        Long userId = com.example.utils.UserContext.getUserId();
        return Result.success(postService.getUserPostStats(userId), "获取用户统计数据成功");
    }

    @Operation(summary = "删除帖子", description = "根据ID删除帖子")
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        postService.deletePost(id);
        return Result.success("删除帖子成功");
    }

    @Operation(summary = "点赞/取消点赞帖子", description = "对帖子进行点赞或取消点赞操作")
    @PostMapping("/like")
    public Result<Boolean> toggleLike(@RequestBody PostIdDTO dto) {
        boolean islike = postService.addOrCancelLike(dto.getPostId());
        return Result.success(islike ? "点赞成功" : "取消点赞成功");
    }

    @Operation(summary = "收藏/取消收藏帖子", description = "对帖子进行收藏或取消收藏操作")
    @PostMapping("/favorite")
    public Result<Boolean> toggleFavorite(@RequestBody PostIdDTO dto) {
        boolean isFavorite = postService.addOrCancelFavorite(dto.getPostId());
        return Result.success(isFavorite ? "收藏成功" : "取消收藏成功");
    }

    @Operation(summary = "增加帖子浏览量", description = "帖子被浏览时调用，浏览量+1")
    @PostMapping("/view")
    public Result<Void> increaseViewCount(@RequestBody PostIdDTO dto) {
        postService.increaseViewCount(dto.getPostId());
        return Result.success("浏览次数+1");
    }
}
