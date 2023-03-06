package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.Container;
import ru.te4rus.auditor.domain.Item;
import ru.te4rus.auditor.exception.ContainerNotFoundException;
import ru.te4rus.auditor.repository.ContainerRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;

    public Container findById(@NonNull Long id) {
        return containerRepository.findById(id).orElseThrow(
                () -> new ContainerNotFoundException(String.format("Контейнер с id:%s не был найден", id))
        );
    }

    public List<Container> findByItem(@NonNull Item item) {
        return containerRepository.findAllContainerByItem(item);
    }

    public Container add(@NonNull Container container) {
        return containerRepository.save(container);
    }

    public Container update(@NonNull Container updatedContainer) {
        Container containerInDb = findById(updatedContainer.getId());

        if (updatedContainer.getFullContainerWeight() != null) {
            containerInDb.setFullContainerWeight(updatedContainer.getFullContainerWeight());
        }

        if (updatedContainer.getEmptyContainerWeight() != null) {
            containerInDb.setEmptyContainerWeight(updatedContainer.getEmptyContainerWeight());
        }

        if (updatedContainer.getContainerCapacity() != null) {
            containerInDb.setContainerCapacity(updatedContainer.getContainerCapacity());
        }

        containerRepository.flush();

        return containerInDb;
    }

    public void deleteById(@NonNull Long id) {
        containerRepository.deleteById(id);
    }

}
