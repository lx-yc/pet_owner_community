package com.example.dto.comment;

import lombok.Data;

@Data
public class CommentCursorQuery {

    private Long postId;

    // 游标：上一页最后一条评论的 id
    private Long lastId;

    // 每页条数，默认 10
    private Integer pageSize;
}
