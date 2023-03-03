package ru.te4rus.auditor.dto.item;

import lombok.NonNull;
import ru.te4rus.auditor.domain.Item;

public class ItemMapper {

    public static Item toItem(@NonNull ItemCreateDto createDto) {
        Item item = new Item();
        item.setName(createDto.getName());
        item.setDescription(createDto.getDescription());

        return item;
    }

    public static Item toItem(@NonNull ItemUpdateDto updateDto) {
        Item item = new Item();
        item.setId(updateDto.getId());
        item.setName(updateDto.getName());
        item.setDescription(updateDto.getDescription());

        return item;
    }

    public static ItemDto toItemDto(@NonNull Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setStorageId(item.getStorage().getId());

        return itemDto;
    }

}
