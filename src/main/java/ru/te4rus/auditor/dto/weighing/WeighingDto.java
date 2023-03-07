package ru.te4rus.auditor.dto.weighing;

import lombok.Data;

@Data
public class WeighingDto {

    private long id;

    private long containerId;

    private long revisionId;

    private double weight;

}
