package ru.te4rus.auditor.dto.weighing;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeighingCreateDto {

    @NotNull
    private Long containerId;

    @NotNull
    private Long revisionId;

    @NotNull
    private Double weight;

}
