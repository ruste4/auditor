package ru.te4rus.auditor.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.te4rus.auditor.domain.Revision;

import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {
    List<Revision> findAllRevisionByStorageId(@NonNull Long storageId);
}
