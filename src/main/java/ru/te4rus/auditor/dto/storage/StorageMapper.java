package ru.te4rus.auditor.dto.storage;

import lombok.NonNull;
import ru.te4rus.auditor.domain.Storage;

public class StorageMapper {

    public static Storage toStorage(@NonNull StorageCreateDto createDto) {
        Storage storage = new Storage();
        storage.setName(createDto.getName());
        storage.setAddress(createDto.getAddress());

        return storage;
    }

    public static Storage toStorage(@NonNull StorageUpdateDto updateDto) {
        Storage storage = new Storage();
        storage.setId(updateDto.getId());
        storage.setName(updateDto.getName());
        storage.setAddress(updateDto.getAddress());

        return storage;
    }

    public static StorageDto toStorageDto(@NonNull Storage storage) {
        StorageDto storageDto = new StorageDto();
        StorageDto.User user = new StorageDto.User();

        user.setId(storage.getUser().getId());
        user.setLogin(storage.getUser().getLogin());

        storageDto.setId(storage.getId());
        storageDto.setName(storage.getName());
        storageDto.setAddress(storage.getAddress());
        storageDto.setUser(user);

        return storageDto;
    }

}
