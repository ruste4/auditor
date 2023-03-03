package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.dto.storage.StorageCreateDto;
import ru.te4rus.auditor.dto.storage.StorageDto;
import ru.te4rus.auditor.dto.storage.StorageMapper;
import ru.te4rus.auditor.dto.storage.StorageUpdateDto;
import ru.te4rus.auditor.service.AccessChecker;
import ru.te4rus.auditor.service.AuthService;
import ru.te4rus.auditor.service.StorageService;
import ru.te4rus.auditor.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageApiManager {

    private final StorageService storageService;
    private final UserService userService;
    private final AuthService authService;

    public StorageDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s запросил склад по id:%s", authInfo.getPrincipal(), id));
        Storage storage = storageService.findById(id);
        AccessChecker.check(storage, authInfo);

        return StorageMapper.toStorageDto(storage);
    }

    public List<StorageDto> findByUser(@NonNull User user) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format(
                        "Пользователь %s запросил список складов по пользователю %s",
                        authInfo.getPrincipal(),
                        user.getLogin()
                )
        );
        AccessChecker.check(user, authInfo);

        return storageService.findByUser(user).stream()
                .map(StorageMapper::toStorageDto)
                .toList();
    }

    public StorageDto addStorage(@NonNull StorageCreateDto createDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s добавляет склад: %s", authInfo.getPrincipal(), createDto));
        User user = userService.findByLogin(authInfo.getPrincipal());
        Storage storage = StorageMapper.toStorage(createDto);
        storage.setUser(user);

        return StorageMapper.toStorageDto(storageService.addStorage(storage));
    }

    public StorageDto updateStorage(@NonNull StorageUpdateDto updateDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format(
                        "Пользователь %s обновляет склад id:%s на %s",
                        authInfo.getPrincipal(),
                        updateDto.getId(),
                        updateDto
                )
        );
        Storage storage = storageService.findById(updateDto.getId());
        Storage updatedStorage = StorageMapper.toStorage(updateDto);
        AccessChecker.check(storage, authInfo);

        return StorageMapper.toStorageDto(storageService.updateStorage(updatedStorage));
    }

    public void deleteById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s удаляет склад по id:%s", authInfo.getPrincipal(), id));
        Storage storage = storageService.findById(id);
        AccessChecker.check(storage, authInfo);
        storageService.deleteById(id);
    }

}
