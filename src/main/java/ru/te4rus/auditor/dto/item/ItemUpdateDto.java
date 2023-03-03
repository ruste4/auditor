package ru.te4rus.auditor.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateDto {

    @NotNull
    private Long id;

    private String name;

    private String description;

}
