package ru.te4rus.auditor.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.Item;
import ru.te4rus.auditor.domain.Storage;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStorage(@NonNull Storage storage);
}
