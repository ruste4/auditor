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
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.exception.StorageNotFoundException;

import java.util.Collection;
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
class StorageServiceTest {

    @Autowired
    private final StorageService storageService;

    @Autowired
    private final TestEntityManager entityManager;

    private static AtomicLong idHolder;

    @BeforeAll
    public static void init() {
        idHolder = new AtomicLong();
    }

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    private final Supplier<User> userSupplier = () -> {
        User user = new User();
        long userId = idHolder.getAndIncrement();

        user.setLogin("login-" + userId);
        user.setFirstname("firstname-" + userId);
        user.setLastname("lastname-" + userId);

        return user;
    };

    private User createAndPersistUser() {
        return entityManager.persist(userSupplier.get());
    }

    private Storage createAndPersistStorage() {
        return createAndPersistStorage(createAndPersistUser());
    }

    private Storage createAndPersistStorage(@NonNull User user) {
        Storage storage = new Storage();
        long storageId = idHolder.getAndIncrement();

        storage.setName("Storage name - " + storageId);
        storage.setAddress("Storage address - " + storageId);
        storage.setUser(user);

        return entityManager.persist(storage);
    }

    @Test
    public void addStorageSuccess() {
        User user = createAndPersistUser();
        Storage storage = new Storage();
        storage.setName("Test add storage");
        storage.setAddress("Test address");
        storage.setUser(user);

        Storage addedStorage = storageService.addStorage(storage);
        Storage found = entityManager.find(Storage.class, addedStorage.getId());

        assertAll(
                () -> assertEquals(found.getName(), storage.getName()),
                () -> assertEquals(found.getAddress(), storage.getAddress()),
                () -> assertNotNull(found.getId())
        );
    }

    @Test
    public void findByIdSuccess() {
        Storage storage = createAndPersistStorage();

        assertNotNull(storageService.findById(storage.getId()));
    }

    @Test
    public void findByIdFailStorageNotFound() {
        assertThrows(StorageNotFoundException.class, () -> storageService.findById(100L));
    }

    @Test
    public void findAllByUserSuccess() {
        User user1 = createAndPersistUser();
        User user2 = createAndPersistUser();

        Storage firstStorageForUser1 = createAndPersistStorage(user1);
        Storage secondStorageForUser1 = createAndPersistStorage(user1);
        Storage storageForUser2 = createAndPersistStorage(user2);

        Collection<Storage> storagesForUser1 = List.of(firstStorageForUser1, secondStorageForUser1);
        Collection<Storage> storagesForUser2 = List.of(storageForUser2);


        assertAll(
                () -> assertEquals(storagesForUser1, storageService.findByUser(user1)),
                () -> assertEquals(storagesForUser2, storageService.findByUser(user2))
        );
    }

    @Test
    public void updateStorageNameSuccess() {
        Storage storage = createAndPersistStorage();
        String updatedName = "UpdatedStorageName";
        Storage updatedStorage = new Storage();
        updatedStorage.setId(storage.getId());
        updatedStorage.setName(updatedName);
        storageService.updateStorage(updatedStorage);
        Storage found = entityManager.find(Storage.class, storage.getId());

        assertEquals(found.getName(), updatedName);
    }

    @Test
    public void updateStorageAddressSuccess() {
        Storage storage = createAndPersistStorage();
        String updatedAddress = "UpdatedStorageAddress";
        Storage updatedStorage = new Storage();
        updatedStorage.setId(storage.getId());
        updatedStorage.setAddress(updatedAddress);
        storageService.updateStorage(updatedStorage);
        Storage found = entityManager.find(Storage.class, storage.getId());

        assertEquals(found.getAddress(), updatedAddress);
    }

    @Test
    public void deleteByIdSuccess() {
        Storage storage = createAndPersistStorage();
        User user = storage.getUser();
        storageService.deleteById(storage.getId());

        assertNull(entityManager.find(Storage.class, storage.getId()));
    }
}