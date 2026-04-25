package com.example.utils;

import java.time.LocalDate;

public class SeasonUtils {
    // 获取当前季节：spring、summer、autumn、winter
    public static String getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();
        return switch (month) {
            case 3, 4, 5 -> "spring";   // 春
            case 6, 7, 8 -> "summer";   // 夏
            case 9,10,11 -> "autumn";   // 秋
            case 12,1,2  -> "winter";   // 冬
            default -> "spring";
        };
    }
}
