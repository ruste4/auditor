package ru.te4rus.auditor.repository;


import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.domain.User;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findAllByUser(@NonNull User user);

}
