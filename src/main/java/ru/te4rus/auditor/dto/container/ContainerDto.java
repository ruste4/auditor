package ru.te4rus.auditor.dto.container;

import lombok.Data;

@Data
public class ContainerDto {

    private Long id;

    private Long itemId;

    private Double fullContainerWeight;

    private Double emptyContainerWeight;

    private Double containerCapacity;

}
