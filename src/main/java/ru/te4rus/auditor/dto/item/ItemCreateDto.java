package ru.te4rus.auditor.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateDto {

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Long storageId;

}
