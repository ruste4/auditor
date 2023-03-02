package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.dto.user.UserCreateDto;
import ru.te4rus.auditor.dto.user.UserDto;
import ru.te4rus.auditor.dto.user.UserMapper;
import ru.te4rus.auditor.dto.user.UserUpdateDto;
import ru.te4rus.auditor.service.AccessChecker;
import ru.te4rus.auditor.service.AuthService;
import ru.te4rus.auditor.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApiManager {

    private final UserService userService;
    private final AuthService authService;

    public UserDto addUser(@NonNull UserCreateDto createDto) {
        log.debug(String.format("Регистроация пользователя: %s", createDto));
        User newUser = UserMapper.toUser(createDto);

        return UserMapper.toUserDto(userService.add(newUser));
    }

    public UserDto updateUser(@NonNull UserUpdateDto updateDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        User user = userService.findById(updateDto.getId());
        log.debug(
                String.format(
                        "Пользователь %s обновляет пользователя с login:%s на: %s",
                        authInfo.getPrincipal(),
                        user.getLogin(),
                        updateDto
                )
        );
        AccessChecker.check(user, authInfo);
        User updatedUser = UserMapper.toUser(updateDto);

        return UserMapper.toUserDto(userService.update(updatedUser));
    }

    public UserDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        User user = userService.findById(id);
        log.debug(String.format("Пользователь с login:%s ищнт пользователя по id:%s", authInfo.getPrincipal(), id));
        AccessChecker.check(user, authInfo);

        return UserMapper.toUserDto(user);
    }

    public void deleteUserById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        User user = userService.findById(id);
        log.debug(String.format("Пользователь %s удаляет пользователя %s", authInfo.getPrincipal(), user.getLogin()));
        AccessChecker.check(user, authInfo);
        userService.deleteUserById(id);
    }

}
