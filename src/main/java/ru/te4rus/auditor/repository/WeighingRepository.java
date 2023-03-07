package ru.te4rus.auditor.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Weighing;

import java.util.List;

public interface WeighingRepository extends JpaRepository<Weighing, Long> {
    List<Weighing> findAllByRevision(@NonNull Revision revision);
}
