package com.codingshuttle.projects.airBnb.dto;

import com.codingshuttle.projects.airBnb.entity.User;
import com.codingshuttle.projects.airBnb.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
