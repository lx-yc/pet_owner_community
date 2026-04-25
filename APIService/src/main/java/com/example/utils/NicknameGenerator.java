package com.example.utils;

import java.util.Random;

public class NicknameGenerator {
    private static final Random random = new Random();
    private static final String PREFIX = "用户";
    // 剔除：0 O l 1 I 这些容易看错的字符
    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

    /**
     * 生成：用户 + 8位 字符（数字+字母）
     * 示例：用户7sK9pR2x
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }
}
