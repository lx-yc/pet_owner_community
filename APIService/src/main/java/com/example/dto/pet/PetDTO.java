package com.example.dto.pet;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetDTO {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private LocalDate birthday;
    private Integer age;
    private String avatar;
    private String info;

}
