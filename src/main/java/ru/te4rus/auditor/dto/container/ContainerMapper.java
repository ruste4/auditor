package ru.te4rus.auditor.dto.container;

import lombok.NonNull;
import ru.te4rus.auditor.domain.Container;

public class ContainerMapper {

    public static Container toContainer(@NonNull ContainerCreateDto createDto) {
        Container container = new Container();
        container.setFullContainerWeight(createDto.getFullContainerWeight());
        container.setEmptyContainerWeight(createDto.getEmptyContainerWeight());
        container.setContainerCapacity(createDto.getContainerCapacity());

        return container;
    }

    public static Container toContainer(@NonNull ContainerUpdateDto updateDto) {
        Container container = new Container();
        container.setId(updateDto.getId());
        container.setFullContainerWeight(updateDto.getFullContainerWeight());
        container.setEmptyContainerWeight(updateDto.getEmptyContainerWeight());
        container.setContainerCapacity(updateDto.getContainerCapacity());

        return container;
    }

    public static ContainerDto toContainerDto(@NonNull Container container) {
        ContainerDto containerDto = new ContainerDto();
        containerDto.setId(container.getId());
        containerDto.setItemId(container.getItem().getId());
        containerDto.setFullContainerWeight(container.getFullContainerWeight());
        containerDto.setEmptyContainerWeight(container.getEmptyContainerWeight());
        containerDto.setContainerCapacity(container.getContainerCapacity());

        return containerDto;
    }

}
