package com.example.vo.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private List<T> list;       // 当前页数据列表
    private Long total;         // 总条数
    private Integer pages;      // 总页数
    private Integer pageNum;    // 当前页
    private Integer pageSize;   // 每页条数
    private boolean hasMore;    // 是否有下一页
}
