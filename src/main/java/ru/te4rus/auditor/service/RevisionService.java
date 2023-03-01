package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.exception.RevisionNotFoundException;
import ru.te4rus.auditor.repository.RevisionRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionRepository revisionRepository;

    public Revision findById(@NonNull Long id) {
        return revisionRepository.findById(id).orElseThrow(
                () -> new RevisionNotFoundException(String.format("Ревизия с id:%s не найдена", id))
        );
    }

    public List<Revision> findByStorageId(@NonNull Long storageId) {
        return revisionRepository.findAllRevisionByStorageId(storageId);
    }

    public Revision addRevision(@NonNull Revision newRevision) {
        return revisionRepository.save(newRevision);
    }

    public Revision updateRevision(@NonNull Revision updatedRevision) {
        Revision revisionInDB = findById(updatedRevision.getId());

        if (updatedRevision.getDate() != null) {
            revisionInDB.setDate(updatedRevision.getDate());
        }

        revisionRepository.flush();

        return revisionInDB;
    }

    public void deleteById(@NonNull Long id) {
        revisionRepository.deleteById(id);
    }

}
