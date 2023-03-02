package ru.te4rus.auditor.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDto {

    @NotNull
    private Long id;

    private String login;

    private String firstname;

    private String lastname;

}
