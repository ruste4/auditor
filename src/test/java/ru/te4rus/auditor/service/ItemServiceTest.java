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
import org.springframework.security.access.AccessDeniedException;
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
        User user = item.getStorage().getUser();
        JwtAuthentication authInfo = generateAuthInfo(user.getLogin());

        assertNotNull(itemService.findById(item.getId(), authInfo));
    }

    @Test
    public void findByIdFailItemNotFound() {
        User user = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(user.getLogin());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(100L, authInfo));
    }

    @Test
    public void findByIdFailWithAnotherUser() {
        Item item = createAndPersistItem();
        User anotherUser = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());

        assertThrows(AccessDeniedException.class, () -> itemService.findById(item.getId(), authInfo));
    }

    @Test
    public void findByIdSuccessWithAnotherUserIsAdmin() {
        Item item = createAndPersistItem();
        User admin = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(admin.getLogin());
        authInfo.setRoles(Collections.singleton(ERole.ADMIN));

        assertNotNull(itemService.findById(item.getId(), authInfo));
    }

    @Test
    public void findByStorageSuccess() {
        Storage storage1 = createAndPersistStorage();
        Storage storage2 = createAndPersistStorage();
        JwtAuthentication authInfoByUserOfStorage1 = generateAuthInfo(storage1.getUser().getLogin());
        JwtAuthentication authInfoByUserOfStorage2 = generateAuthInfo(storage2.getUser().getLogin());

        Item firstItemForStorage1 = createAndPersistItem(storage1);
        Item secondItemForStorage1 = createAndPersistItem(storage1);
        Item itemForStorage2 = createAndPersistItem(storage2);

        List<Item> itemsForStorage1 = List.of(firstItemForStorage1, secondItemForStorage1);
        List<Item> itemsForStorage2 = List.of(itemForStorage2);

        assertAll(
                () -> assertEquals(itemsForStorage1, itemService.findByStorage(storage1, authInfoByUserOfStorage1)),
                () -> assertEquals(itemsForStorage2, itemService.findByStorage(storage2, authInfoByUserOfStorage2))
        );
    }

    @Test
    public void findByStorageFailWithAnotherUser() {
        Item item = createAndPersistItem();
        User anotherUser = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());


        assertThrows(AccessDeniedException.class, () -> itemService.findByStorage(item.getStorage(), authInfo));
    }

    @Test
    public void findByStorageSuccessWithAnotherUserIsAdmin() {
        Item item = createAndPersistItem();
        User admin = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(admin.getLogin());
        authInfo.setRoles(Collections.singleton(ERole.ADMIN));

        assertNotNull(itemService.findByStorage(item.getStorage(), authInfo));
    }

    @Test
    public void updateItemNameSuccess() {
        Item item = createAndPersistItem();
        JwtAuthentication authInfo = generateAuthInfo(item.getStorage().getUser().getLogin());
        String updatedName = "updatedItemName";
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName(updatedName);

        itemService.updateItem(updatedItem, authInfo);
        Item found = entityManager.find(Item.class, item.getId());

        assertEquals(found.getName(), updatedName);
    }

    @Test
    public void updatedItemDescriptionSuccess() {
        Item item = createAndPersistItem();
        JwtAuthentication authInfo = generateAuthInfo(item.getStorage().getUser().getLogin());
        String updatedDescription = "updatedDescription";
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setDescription(updatedDescription);

        itemService.updateItem(updatedItem, authInfo);
        Item found = entityManager.find(Item.class, item.getId());

        assertEquals(found.getDescription(), updatedDescription);
    }

    @Test
    public void updatedItemFailWithAnotherUser() {
        Item item = createAndPersistItem();
        User anotherUser = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("updatedName");

        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(updatedItem, authInfo));
    }

    @Test
    public void updatedItemSuccessWithAnotherUserIsAdmin() {
        Item item = createAndPersistItem();
        User admin = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(admin.getLogin());
        authInfo.setRoles(Collections.singleton(ERole.ADMIN));
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("updatedName");

        assertDoesNotThrow(() -> itemService.updateItem(updatedItem, authInfo));
    }

    @Test
    public void deleteByIdSuccess() {
        Item item = createAndPersistItem();
        JwtAuthentication authInfo = generateAuthInfo(item.getStorage().getUser().getLogin());
        itemService.deleteById(item.getId(), authInfo);

        assertNull(entityManager.find(Item.class, item.getId()));
    }

    @Test
    public void deleteByIdFailWithAnotherUser() {
        Item item = createAndPersistItem();
        User anotherUser = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());

        assertThrows(AccessDeniedException.class, () -> itemService.deleteById(item.getId(), authInfo));
    }

    @Test
    public void deleteByIdWithAnotherUserIsAdmin() {
        Item item = createAndPersistItem();
        User anotherUser = createAndPersistUser();
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());
        authInfo.setRoles(Collections.singleton(ERole.ADMIN));

        assertDoesNotThrow(() -> itemService.deleteById(item.getId(), authInfo));
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

    private JwtAuthentication generateAuthInfo(String login) {
        JwtAuthentication authInfo = new JwtAuthentication();
        authInfo.setLogin(login);
        authInfo.setRoles(Collections.singleton(ERole.USER));
        return authInfo;
    }
}