package com.example.dto.page;

import lombok.Data;

@Data
public class PageDTO {
    private Integer pageNum;    // 当前页码，从 1 开始
    private Integer pageSize;   // 每页条数
    private String keyword;     // 搜索关键词
}