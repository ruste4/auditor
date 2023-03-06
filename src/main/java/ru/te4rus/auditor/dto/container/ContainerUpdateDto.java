package ru.te4rus.auditor.dto.container;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContainerUpdateDto {

    @NotNull
    private Long id;

    private Double fullContainerWeight;

    private Double emptyContainerWeight;

    private Double containerCapacity;

}
