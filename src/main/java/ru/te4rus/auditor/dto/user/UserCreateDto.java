package ru.te4rus.auditor.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserCreateDto {

    @NotEmpty
    private String login;

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String lastname;

}
