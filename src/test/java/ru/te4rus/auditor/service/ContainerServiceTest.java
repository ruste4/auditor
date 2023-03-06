package ru.te4rus.auditor.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.Container;
import ru.te4rus.auditor.domain.Item;
import ru.te4rus.auditor.exception.ContainerNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ContainerServiceTest {

    @Autowired
    private final ContainerService containerService;

    @Autowired
    private final TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
    }

    @Test
    public void addContainerSuccess() {
        Container container = EntityGenerator.createContainer(entityManager);
        Container persistedContainer = containerService.add(container);
        Container found = entityManager.find(Container.class, persistedContainer.getId());

        assertNotNull(found);
    }

    @Test
    public void findByIdSuccess() {
        Container container = EntityGenerator.createAndPersistContainer(entityManager);

        assertNotNull(containerService.findById(container.getId()));
    }

    @Test
     public void findByIdFailContainerNotFound() {
        assertThrows(ContainerNotFoundException.class, () -> containerService.findById(100L));
    }

    @Test
    public void findByItemSuccess() {
        Item item1 = EntityGenerator.createAndPersistItem(entityManager);
        Item item2 = EntityGenerator.createAndPersistItem(entityManager);
        Container firstContainerForItem1 = EntityGenerator.createAndPersistContainer(entityManager, item1);
        Container secondContainerForItem1 = EntityGenerator.createAndPersistContainer(entityManager, item1);
        Container firstContainerForItem2 = EntityGenerator.createAndPersistContainer(entityManager, item2);
        List<Container> containersForItem1 = List.of(firstContainerForItem1, secondContainerForItem1);
        List<Container> containersForItem2 = List.of(firstContainerForItem2);

        assertAll(
                () -> assertEquals(containersForItem1, containerService.findByItem(item1)),
                () -> assertEquals(containersForItem2, containerService.findByItem(item2))
        );
    }

    @Test
    public void updateSuccessFullContainerWeight() {
        Container container = EntityGenerator.createAndPersistContainer(entityManager);
        Double updatedFullContainerWeight = 0.1;
        Container updatedContainer = new Container();
        updatedContainer.setId(container.getId());
        updatedContainer.setFullContainerWeight(updatedFullContainerWeight);
        containerService.update(updatedContainer);
        Container found = entityManager.find(Container.class, container.getId());

        assertEquals(found.getFullContainerWeight(), updatedFullContainerWeight);
    }

    @Test
    public void updateSuccessEmptyContainerWeight() {
        Container container = EntityGenerator.createAndPersistContainer(entityManager);
        Double updatedEmptyContainerWeight = 0.05;
        Container updatedContainer = new Container();
        updatedContainer.setId(container.getId());
        updatedContainer.setEmptyContainerWeight(updatedEmptyContainerWeight);
        containerService.update(updatedContainer);
        Container found = entityManager.find(Container.class, container.getId());

        assertEquals(found.getEmptyContainerWeight(), updatedEmptyContainerWeight);
    }

    @Test
    public void updateSuccessContainerCapacity() {
        Container container = EntityGenerator.createAndPersistContainer(entityManager);
        Double updatedContainerCapacity = 0.83;
        Container updatedContainer = new Container();
        updatedContainer.setId(container.getId());
        updatedContainer.setContainerCapacity(updatedContainerCapacity);
        containerService.update(updatedContainer);
        Container found = entityManager.find(Container.class, container.getId());

        assertEquals(found.getContainerCapacity(), updatedContainerCapacity);
    }

    @Test
    public void deleteByIdSuccess() {
        Container container = EntityGenerator.createAndPersistContainer(entityManager);
        containerService.deleteById(container.getId());
        Container found = entityManager.find(Container.class, container.getId());

        assertNull(found);
    }
}