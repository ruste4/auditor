package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.*;
import ru.te4rus.auditor.exception.ItemNotFoundException;
import ru.te4rus.auditor.repository.ItemRepository;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public Item addItem(@NonNull Item item) {
        return itemRepository.save(item);
    }

    public List<Item> findByStorage(@NonNull Storage storage) {
        return itemRepository.findAllByStorage(storage);
    }

    public Item updateItem(@NonNull Item updatedItem) {
        Item itemInDB = findById(updatedItem.getId());

        if (updatedItem.getName() != null) {
            itemInDB.setName(updatedItem.getName());
        }

        if (updatedItem.getDescription() != null) {
            itemInDB.setDescription(updatedItem.getDescription());
        }

        itemRepository.flush();

        return itemInDB;
    }

    public void deleteById(@NonNull Long id) {
        itemRepository.deleteById(id);
    }

    public Item findById(@NonNull Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Продукт с id:%s не найден", id)));
    }

}
