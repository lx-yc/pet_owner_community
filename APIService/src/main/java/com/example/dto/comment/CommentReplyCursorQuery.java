package com.example.dto.comment;

import lombok.Data;

@Data
public class CommentReplyCursorQuery {
    private Long parentId;

    private Long lastId;

    private Integer pageSize;
}
