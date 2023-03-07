package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Weighing;
import ru.te4rus.auditor.exception.WeighingNotFoundException;
import ru.te4rus.auditor.repository.WeighingRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WeighingService {

    private final WeighingRepository weighingRepository;

    public Weighing findById(@NonNull Long id) {
        return weighingRepository.findById(id).orElseThrow(
                () -> new WeighingNotFoundException(String.format("Взвешивание по id:%s не найден", id))
        );
    }

    public List<Weighing> findByRevision(@NonNull Revision revision) {
        return weighingRepository.findAllByRevision(revision);
    }

    public Weighing add(@NonNull Weighing weighing) {
        return weighingRepository.save(weighing);
    }

    public void deleteById(@NonNull Long id) {
        weighingRepository.deleteById(id);
    }

}
