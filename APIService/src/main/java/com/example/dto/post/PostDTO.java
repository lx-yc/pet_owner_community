package com.example.dto.post;

import lombok.Data;

@Data
public class PostDTO {
    private Long userId;
    private Long id;
    private String title;
    private String content;
    private String images;
    private String type;
    private String tags;
    private Integer isAnonymous;

    private String season;
}
