package ru.te4rus.auditor.service;

import lombok.NonNull;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.te4rus.auditor.domain.*;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

public class EntityGenerator {

    private final static AtomicLong POSTFIX_HOLDER = new AtomicLong();


    public static User createUser() {
        User user = new User();
        long userId = POSTFIX_HOLDER.getAndIncrement();
        user.setLogin("login-" + userId);
        user.setLastname("lastname-" + userId);
        user.setFirstname("firstname-" + userId);

        return user;
    }

    public static Storage createStorage(@NonNull TestEntityManager entityManager) {
        Storage storage = new Storage();
        long storageId = POSTFIX_HOLDER.getAndIncrement();
        storage.setName("storageName-" + storageId);
        storage.setAddress("storageAddress-" + storageId);
        storage.setUser(createAndPersistUser(entityManager));

        return storage;
    }

    public static Item createItem(@NonNull TestEntityManager entityManager) {
        Item item = new Item();
        long itemId = POSTFIX_HOLDER.getAndIncrement();
        item.setName("itemName-" + itemId);
        item.setDescription("itemDescription-" + itemId);
        item.setStorage(createAndPersistStorage(entityManager));

        return item;
    }

    public static Revision createRevision(@NonNull TestEntityManager entityManager) {
        Revision revision = new Revision();
        revision.setDate(LocalDate.now());
        revision.setStorage(createAndPersistStorage(entityManager));

        return revision;
    }

    public static Revision createRevision(@NonNull Storage storage) {
        Revision revision = new Revision();
        revision.setDate(LocalDate.now());
        revision.setStorage(storage);

        return revision;
    }

    public static Revision createAndPersistRevision(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createRevision(entityManager));
    }

    public static Revision createAndPersistRevision(@NonNull TestEntityManager entityManager, @NonNull Storage storage) {
        return entityManager.persist(createRevision(storage));
    }

    public static Item createAndPersistItem(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createItem(entityManager));
    }

    public static Item createAndPersistItem(@NonNull TestEntityManager entityManager, @NonNull Storage storage) {
        Item item = createItem(entityManager);
        item.setStorage(storage);
        entityManager.persist(item);

        return item;
    }

    public static Storage createAndPersistStorage(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createStorage(entityManager));
    }

    public static User createAndPersistUser(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createUser());
    }

}
