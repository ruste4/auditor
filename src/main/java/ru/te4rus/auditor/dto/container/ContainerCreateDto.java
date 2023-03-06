package ru.te4rus.auditor.dto.container;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ContainerCreateDto {

    @NotNull
    private final Long itemId;

    @NotNull
    @PositiveOrZero
    private Double fullContainerWeight;

    @NotNull
    @PositiveOrZero
    private Double emptyContainerWeight;

    @NotNull
    @PositiveOrZero
    private Double containerCapacity;

}
