package ru.te4rus.auditor.dto.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StorageUpdateDto {

    @NotNull
    private Long id;

    private String name;

    private String address;

}
