package ru.te4rus.auditor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

}
