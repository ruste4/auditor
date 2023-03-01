package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;
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

    /**
     * Найти склад по id
     *
     * @throws AccessDeniedException если текущий пользователь не владец склада и не админ
     */
    public Storage findById(@NonNull Long id, @NonNull JwtAuthentication authInfo) {
        Storage storage = findById(id);
        checkAccess(storage, authInfo);

        return storage;
    }

    public List<Storage> findByUser(@NonNull User user) {
        return storageRepository.findAllByUser(user);
    }

    public Storage addStorage(@NonNull Storage newStorage) {
        return storageRepository.save(newStorage);
    }

    /**
     * Обновить склад
     *
     * @param updatedStorage - обновленный склад
     * @param authInfo       - информация об авторизованном пользователе
     * @return обновленный склад
     * @throws AccessDeniedException если залогиннены пользователь изменяет чужой склад не являясь при это админом
     */
    public Storage updateStorage(@NonNull Storage updatedStorage, @NonNull JwtAuthentication authInfo) {
        Storage storageInDB = findById(updatedStorage.getId());

        checkAccess(storageInDB, authInfo);

        if (updatedStorage.getAddress() != null) {
            storageInDB.setAddress(updatedStorage.getAddress());
        }

        if (updatedStorage.getName() != null) {
            storageInDB.setName(updatedStorage.getName());
        }

        storageRepository.flush();

        return storageInDB;
    }

    /**
     * Удалить склад по id
     *
     * @throws AccessDeniedException если текущий пользователь не админ либо не владец склада
     */
    public void deleteById(@NonNull Long id, @NonNull JwtAuthentication authInfo) {
        Storage storage = findById(id);

        checkAccess(storage, authInfo);

        storageRepository.deleteById(id);
    }

    private void checkAccess(@NonNull Storage storage, @NonNull JwtAuthentication authInfo) {
        String currentUserLogin = authInfo.getPrincipal();
        String storageUserLogin = storage.getUser().getLogin();

        if (!currentUserLogin.equals(storageUserLogin) && !authInfo.getRoles().contains(ERole.ADMIN)) {
            throw new AccessDeniedException(
                    String.format("Пользователь %s не иожет изменять информацию о складах другого пользователя",
                            currentUserLogin));
        }
    }

}
