package com.example.service;

import com.example.dto.comment.CommentCursorQuery;
import com.example.dto.comment.CommentDTO;
import com.example.dto.comment.CommentReplyCursorQuery;
import com.example.entity.Comment;
import com.example.vo.Comment.CommentVO;
import com.example.vo.Comment.ReplyVO;
import com.example.vo.page.CursorPageVO;


public interface CommentService {
    //新增评论
    void insertComment(CommentDTO commentDTO);
    //更新评论
    void updateComment(CommentDTO commentDTO);
    //删除评论
    void deleteById(Long id);
    //点赞
    boolean addOrCancelLike(Long commentId);
    // 一级评论分页
    CursorPageVO<CommentVO> getTopCommentPage(CommentCursorQuery query);
    // 回复分页
    CursorPageVO<ReplyVO> getReplyPage(CommentReplyCursorQuery query);
}
