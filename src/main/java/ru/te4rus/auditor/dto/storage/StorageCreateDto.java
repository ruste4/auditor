package ru.te4rus.auditor.dto.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StorageCreateDto {

    @NotNull
    private String name;

    private String address;

}
