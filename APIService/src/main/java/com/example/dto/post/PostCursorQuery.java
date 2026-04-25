package com.example.dto.post;

import lombok.Data;

/**
 * 帖子列表游标分页查询请求
 */
@Data
public class PostCursorQuery {
    private Long userId;

    private int isMine;

    private String season;

    private Long cursorId;

    private String cursorTime;

    private Integer cursorSeasonPriority;

    private String sortType = "latest";

    private String postType;

    private Integer pageSize = 10;

}
