package ru.te4rus.auditor.dto.item;

import lombok.Data;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Long storageId;

}
