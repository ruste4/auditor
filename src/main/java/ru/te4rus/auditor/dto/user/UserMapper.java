package ru.te4rus.auditor.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import ru.te4rus.auditor.domain.User;

public class UserMapper {

    public static User toUser(@NotNull UserCreateDto createDto) {
        User user = new User();
        user.setLogin(createDto.getLogin());
        user.setFirstname(createDto.getFirstname());
        user.setLastname(createDto.getLastname());

        return user;
    }

    public static User toUser(@NotNull UserUpdateDto updateDto) {
        User user = new User();
        user.setId(updateDto.getId());
        user.setLogin(updateDto.getLogin());
        user.setFirstname(updateDto.getFirstname());
        user.setLastname(updateDto.getLastname());

        return user;
    }

    public static UserDto toUserDto(@NonNull User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());

        return userDto;
    }

}
