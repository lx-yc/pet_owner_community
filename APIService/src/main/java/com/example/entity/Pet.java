package com.example.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Pet {
    /** 宠物ID */
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 宠物名字 */
    private String name;

    /** 宠物类型：狗/猫/其他 */
    private String type;

    /** 宠物生日 */
    private LocalDate birthday;

    /** 宠物年龄 */
    private Integer age;

    /** 宠物头像 */
    private String avatar;

    /** 宠物介绍 */
    private String info;

    /** 创建时间 */
    private LocalDateTime createTime;
}
