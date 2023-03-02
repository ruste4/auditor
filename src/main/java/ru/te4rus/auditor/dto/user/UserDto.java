package ru.te4rus.auditor.dto.user;

import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String login;

    private String firstname;

    private String lastname;

}
