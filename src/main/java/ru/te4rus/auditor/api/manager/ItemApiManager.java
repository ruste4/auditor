package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.Item;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.dto.item.ItemCreateDto;
import ru.te4rus.auditor.dto.item.ItemDto;
import ru.te4rus.auditor.dto.item.ItemMapper;
import ru.te4rus.auditor.dto.item.ItemUpdateDto;
import ru.te4rus.auditor.service.AccessChecker;
import ru.te4rus.auditor.service.AuthService;
import ru.te4rus.auditor.service.ItemService;
import ru.te4rus.auditor.service.StorageService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemApiManager {

    private final ItemService itemService;
    private final AuthService authService;

    private final StorageService storageService;

    public ItemDto addItem(@NonNull ItemCreateDto createDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s добавляет продукт %s", authInfo.getPrincipal(), createDto));
        Storage storage = storageService.findById(createDto.getStorageId());
        AccessChecker.check(storage, authInfo);
        Item newItem = ItemMapper.toItem(createDto);
        newItem.setStorage(storage);

        return ItemMapper.toItemDto(itemService.addItem(newItem));

    }

    public ItemDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s ищет продукт по id:%s", authInfo.getPrincipal(), id));
        Item item = itemService.findById(id);
        AccessChecker.check(item, authInfo);

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> findByStorage(@NonNull Long storageId) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format(
                        "Пользователь %s запросил список продуктов склада с id:%s",
                        authInfo.getPrincipal(),
                        storageId)
        );
        Storage storage = storageService.findById(storageId);
        AccessChecker.check(storage, authInfo);

        return itemService.findByStorage(storage).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public ItemDto updateItem(@NonNull ItemUpdateDto updatedItemDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        Item item = itemService.findById(updatedItemDto.getId());
        Item updatedItem = ItemMapper.toItem(updatedItemDto);
        log.debug(String.format(
                "Пользователь %s обновляет продукт %s на %s", authInfo.getPrincipal(), item, updatedItem
        ));
        AccessChecker.check(item, authInfo);

        return ItemMapper.toItemDto(itemService.updateItem(updatedItem));
    }

    public void deleteById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s удаляет продукт по id:%s", authInfo.getPrincipal(), id));
        Item item = itemService.findById(id);
        AccessChecker.check(item, authInfo);

        itemService.deleteById(id);
    }

}
