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


    public static Container createContainer(@NonNull TestEntityManager entityManager) {
        Container container = new Container();
        Item item  = createAndPersistItem(entityManager);
        container.setItem(item);
        container.setFullContainerWeight(1.2);
        container.setEmptyContainerWeight(0.3);
        container.setContainerCapacity(0.7);

        return container;
    }

    public static Container createContainer(Item item) {
        Container container = new Container();
        container.setItem(item);
        container.setFullContainerWeight(1.2);
        container.setEmptyContainerWeight(0.3);
        container.setContainerCapacity(0.7);

        return container;
    }

    public static Container createAndPersistContainer(@NonNull TestEntityManager entityManager, @NonNull Item item) {
        return entityManager.persist(createContainer(item));
    }

    public static Container createAndPersistContainer(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createContainer(entityManager));
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

    public static Storage createAndPersistStorage(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createStorage(entityManager));
    }

    public static User createAndPersistUser(@NonNull TestEntityManager entityManager) {
        return entityManager.persist(createUser());
    }

}
