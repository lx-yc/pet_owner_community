package com.example.dto.pet;

import lombok.Data;

@Data
public class PetCursorQuery {
    private Long userId;       // 必传：当前用户ID
    private Long cursorId;    // 游标（上一页最后一条id）
    private Integer pageSize; // 每页条数
}
