package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.exception.StorageNotFoundException;
import ru.te4rus.auditor.repository.StorageRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    public Storage findById(@NonNull Long id) {
        return storageRepository.findById(id).orElseThrow(
                () -> new StorageNotFoundException(String.format("Склад с id:%s не найден", id))
        );
    }
    public List<Storage> findByUser(@NonNull User user) {
        return storageRepository.findAllByUser(user);
    }

    public Storage addStorage(@NonNull Storage newStorage) {
        return storageRepository.save(newStorage);
    }

    public Storage updateStorage(@NonNull Storage updatedStorage) {
        Storage storageInDB = findById(updatedStorage.getId());

        if (updatedStorage.getAddress() != null) {
            storageInDB.setAddress(updatedStorage.getAddress());
        }

        if (updatedStorage.getName() != null) {
            storageInDB.setName(updatedStorage.getName());
        }

        storageRepository.flush();

        return storageInDB;
    }

    public void deleteById(@NonNull Long id) {
        storageRepository.deleteById(id);
    }

}
