package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public User update(@NonNull User updatedUser) {
        User userInDB = findById(updatedUser.getId());

        if (updatedUser.getFirstname() != null) {
            userInDB.setFirstname(updatedUser.getFirstname());
        }

        if (updatedUser.getLastname() != null) {
            userInDB.setLastname(updatedUser.getLastname());
        }

        userRepository.flush();

        return userInDB;
    }

    public User findById(@NonNull Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с id:%s не найден", id))
        );
    }

    public User findByLogin(@NonNull String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с login: %s не найден", login))
        );
    }

    public void deleteUserById(@NonNull Long id) {
        userRepository.deleteById(id);
    }

}
