package com.example.vo.page;

import lombok.Data;

import java.util.List;

@Data
public class CursorPageVO<T> {
    private List<T> list;              // 数据列表
    private Long nextCursorId;         // 下一页游标ID
    private String nextCursorTime;     // 下一页游标时间
    private Integer nextCursorSeasonPriority; // 下一页游标季节优先级（0=当前季节，1=其他季节）
    private Boolean isLast = false;    // 是否最后一页
    private Long total = 0L;           // 总数
}
