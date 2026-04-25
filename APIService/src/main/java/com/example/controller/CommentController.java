package com.example.controller;

import com.example.dto.comment.CommentCursorQuery;
import com.example.dto.comment.CommentDTO;
import com.example.dto.comment.CommentReplyCursorQuery;
import com.example.service.CommentService;
import com.example.utils.Result;
import com.example.vo.Comment.CommentVO;
import com.example.vo.Comment.ReplyVO;
import com.example.vo.page.CursorPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理", description = "评论的增删改查、点赞、回复等接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "发表评论", description = "对帖子发表评论或回复其他评论")
    @PostMapping("/insert")
    public Result<Integer> insert(@RequestBody CommentDTO commentDTO) {
        commentService.insertComment(commentDTO);
        return Result.success("新增评论成功");
    }

    @Operation(summary = "更新评论", description = "更新已有评论的内容")
    @PostMapping("/update")
    public Result<Integer> update(@RequestBody CommentDTO commentDTO) {
        commentService.updateComment(commentDTO);
        return Result.success("更新评论成功");
    }

    @Operation(summary = "删除评论", description = "根据ID删除评论")
    @PostMapping("/delete")
    public Result<Integer> delete(@RequestParam Long id) {
        commentService.deleteById(id);
        return Result.success("删除评论成功");
    }

    @Operation(summary = "点赞/取消点赞评论", description = "对评论进行点赞或取消点赞操作")
    @PostMapping("/like")
    public Result<Boolean> toggleLike(@RequestParam Long commentId) {
        boolean islike = commentService.addOrCancelLike(commentId);
        return Result.success(islike ? "点赞成功" : "取消点赞成功");
    }

    @Operation(summary = "获取一级评论列表", description = "分页获取帖子的一级评论（游标分页）")
    @GetMapping("/page/top")
    public Result<CursorPageVO<CommentVO>> pageTopComment(CommentCursorQuery query) {
        return Result.success(commentService.getTopCommentPage(query), "获取一级评论成功");
    }

    @Operation(summary = "获取楼中楼回复列表", description = "分页获取某条评论的回复（游标分页）")
    @GetMapping("/page/reply")
    public Result<CursorPageVO<ReplyVO>> pageReply(CommentReplyCursorQuery query) {
        return Result.success(commentService.getReplyPage(query), "获取楼中楼回复成功");
    }
}
