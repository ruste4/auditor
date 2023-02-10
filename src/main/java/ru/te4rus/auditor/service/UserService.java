package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.exception.UserNotFoundException;
import ru.te4rus.auditor.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User add(@NonNull User newUser) {
        return userRepository.save(newUser);
    }

    /**
     * Обновить пользоателя
     *
     * @param updatedUser обновленный пользователь
     * @param authInfo    информация об авторизованном пользователе (обновить данные пользователя могут только сам
     *                    пользователь либо админ)
     * @return обновленного пользователя
     */
    public User update(@NonNull User updatedUser, @NonNull JwtAuthentication authInfo) {
        User userInDB = findById(updatedUser.getId());
        boolean isNotAdminAndNotOwner = !authInfo.getPrincipal().equals(userInDB.getLogin()) &&
                !authInfo.getRoles().contains(ERole.ADMIN);

        if (isNotAdminAndNotOwner) {
            throw new AccessDeniedException("Отказано в доступе");
        }

        if (updatedUser.getLogin() != null) {
            userInDB.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getFirstname() != null) {
            userInDB.setFirstname(updatedUser.getFirstname());
        }

        if (updatedUser.getLastname() != null) {
            userInDB.setLastname(updatedUser.getLastname());
        }

        userRepository.flush();

        return userInDB;
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с id:%s не найден", id))
        );
    }

    public User findByLogin(@NonNull String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с login: %s не найден", login))
        );
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

}
