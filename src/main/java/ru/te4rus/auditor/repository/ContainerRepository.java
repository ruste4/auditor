package ru.te4rus.auditor.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.Container;
import ru.te4rus.auditor.domain.Item;

import java.util.List;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    List<Container> findAllContainerByItem(@NonNull Item item);

}
