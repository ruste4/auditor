package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.Container;
import ru.te4rus.auditor.domain.Item;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.dto.container.ContainerCreateDto;
import ru.te4rus.auditor.dto.container.ContainerDto;
import ru.te4rus.auditor.dto.container.ContainerMapper;
import ru.te4rus.auditor.dto.container.ContainerUpdateDto;
import ru.te4rus.auditor.service.AccessChecker;
import ru.te4rus.auditor.service.AuthService;
import ru.te4rus.auditor.service.ContainerService;
import ru.te4rus.auditor.service.ItemService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContainerApiManager {

    private final ContainerService containerService;
    private final AuthService authService;
    private final ItemService itemService;

    public ContainerDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s ищет контейнер по id:%s", authInfo.getPrincipal(), id));
        Container container = containerService.findById(id);
        AccessChecker.check(container.getItem(), authInfo);

        return ContainerMapper.toContainerDto(container);
    }

    public List<ContainerDto> findByItem(@NonNull Long itemId) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format("Пользователь %s ищет контейнеры для продукта по id:%s", authInfo.getPrincipal(), itemId)
        );
        Item item = itemService.findById(itemId);
        AccessChecker.check(item, authInfo);

        return containerService.findByItem(item).stream()
                .map(ContainerMapper::toContainerDto)
                .toList();
    }

    public ContainerDto add(@NonNull ContainerCreateDto createDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format(
                        "Пользователь %s для продукта с id:%s добавляет контейнер %s",
                        authInfo.getPrincipal(),
                        createDto.getItemId(),
                        createDto
                )
        );
        Item item = itemService.findById(createDto.getItemId());
        AccessChecker.check(item, authInfo);
        Container newContainer = ContainerMapper.toContainer(createDto);
        newContainer.setItem(item);

        return ContainerMapper.toContainerDto(containerService.add(newContainer));
    }

    public ContainerDto update(@NonNull ContainerUpdateDto updateDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format(
                        "Пользователь %s обновляет контейнер с id:%s на %s",
                        authInfo.getPrincipal(),
                        updateDto.getId(),
                        updateDto
                )
        );
        Container container = containerService.findById(updateDto.getId());
        AccessChecker.check(container.getItem(), authInfo);
        Container updatedContainer = ContainerMapper.toContainer(updateDto);

        return ContainerMapper.toContainerDto(containerService.update(updatedContainer));
    }

    public void deleteById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s удаляет контейнер по id:%s", authInfo.getPrincipal(), id));
        Container container = containerService.findById(id);
        AccessChecker.check(container.getItem(), authInfo);
        containerService.deleteById(id);
    }

}
