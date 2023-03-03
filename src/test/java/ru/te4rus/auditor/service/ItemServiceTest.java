package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.*;
import ru.te4rus.auditor.exception.ItemNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final TestEntityManager entityManager;

    private static AtomicLong idHolder;

    @BeforeAll
    public static void init() {
        idHolder = new AtomicLong();
    }

    @BeforeEach
    public void setUp() {
        entityManager.clear();
    }

    private final Supplier<User> userSupplier = () -> {
        User user = new User();
        long userId = idHolder.getAndIncrement();
        user.setLogin("login-" + userId);
        user.setLastname("lastname-" + userId);
        user.setFirstname("firstname-" + userId);

        return user;
    };

    private final Supplier<Storage> storageSupplier = () -> {
        Storage storage = new Storage();
        long storageId = idHolder.getAndIncrement();
        storage.setName("storageName-" + storageId);
        storage.setAddress("storageAddress-" + storageId);
        storage.setUser(createAndPersistUser());

        return storage;
    };

    private final Supplier<Item> itemSupplier = () -> {
        Item item = new Item();
        long itemId = idHolder.getAndIncrement();
        item.setName("itemName-" + itemId);
        item.setDescription("itemDescription-" + itemId);
        item.setStorage(createAndPersistStorage());

        return item;
    };

    @Test
    public void findByIdSuccess() {
        Item item = createAndPersistItem();

        assertNotNull(itemService.findById(item.getId()));
    }

    @Test
    public void findByIdFailItemNotFound() {
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(100L));
    }

    @Test
    public void findByStorageSuccess() {
        Storage storage1 = createAndPersistStorage();
        Storage storage2 = createAndPersistStorage();

        Item firstItemForStorage1 = createAndPersistItem(storage1);
        Item secondItemForStorage1 = createAndPersistItem(storage1);
        Item itemForStorage2 = createAndPersistItem(storage2);

        List<Item> itemsForStorage1 = List.of(firstItemForStorage1, secondItemForStorage1);
        List<Item> itemsForStorage2 = List.of(itemForStorage2);

        assertAll(
                () -> assertEquals(itemsForStorage1, itemService.findByStorage(storage1)),
                () -> assertEquals(itemsForStorage2, itemService.findByStorage(storage2))
        );
    }

    @Test
    public void updateItemNameSuccess() {
        Item item = createAndPersistItem();
        String updatedName = "updatedItemName";
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName(updatedName);

        itemService.updateItem(updatedItem);
        Item found = entityManager.find(Item.class, item.getId());

        assertEquals(found.getName(), updatedName);
    }

    @Test
    public void updatedItemDescriptionSuccess() {
        Item item = createAndPersistItem();
        String updatedDescription = "updatedDescription";
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setDescription(updatedDescription);

        itemService.updateItem(updatedItem);
        Item found = entityManager.find(Item.class, item.getId());

        assertEquals(found.getDescription(), updatedDescription);
    }

    @Test
    public void deleteByIdSuccess() {
        Item item = createAndPersistItem();
        itemService.deleteById(item.getId());

        assertNull(entityManager.find(Item.class, item.getId()));
    }

    private Item createAndPersistItem() {
        return entityManager.persist(itemSupplier.get());
    }

    private Item createAndPersistItem(@NonNull Storage storage) {
        Item item = itemSupplier.get();
        item.setStorage(storage);
        entityManager.persist(item);

        return item;
    }

    private Storage createAndPersistStorage() {
        return entityManager.persist(storageSupplier.get());
    }

    private User createAndPersistUser() {
        return entityManager.persist(userSupplier.get());
    }
}