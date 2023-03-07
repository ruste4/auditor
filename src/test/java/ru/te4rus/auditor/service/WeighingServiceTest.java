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
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Weighing;
import ru.te4rus.auditor.exception.WeighingNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class WeighingServiceTest {

    @Autowired
    private final WeighingService weighingService;

    @Autowired
    private final TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    public void findByIdSuccess() {
        Weighing weighing = EntityGenerator.createAndPersistWeighing(entityManager);

        assertNotNull(weighingService.findById(weighing.getId()));
    }

    @Test
    public void findByIdFailWeighingNotFoundException() {
        assertThrows(WeighingNotFoundException.class, () -> weighingService.findById(100L));
    }

    @Test
    public void findByRevisionSuccess() {
        Revision revision1 = EntityGenerator.createAndPersistRevision(entityManager);
        Revision revision2 = EntityGenerator.createAndPersistRevision(entityManager);
        Weighing firstWeighingForRevision1 = EntityGenerator.createAndPersistWeighing(entityManager, revision1);
        Weighing secondWeighingForRevision1 = EntityGenerator.createAndPersistWeighing(entityManager, revision1);
        Weighing firstWeighingForRevision2 = EntityGenerator.createAndPersistWeighing(entityManager, revision2);
        List<Weighing> weighingForRevision1 = List.of(firstWeighingForRevision1, secondWeighingForRevision1);
        List<Weighing> weighingForRevision2 = List.of(firstWeighingForRevision2);

        assertAll(
                () -> assertEquals(weighingService.findByRevision(revision1), weighingForRevision1),
                () -> assertEquals(weighingService.findByRevision(revision2), weighingForRevision2)
        );
    }

    @Test
    public void addSuccess() {
        Weighing weighing = EntityGenerator.createWeighing(entityManager);
        Weighing addedWeighing = weighingService.add(weighing);
        Weighing found = entityManager.find(Weighing.class, addedWeighing.getId());

        assertNotNull(found);
    }

    @Test
    public void deleteById() {
        Weighing weighing = EntityGenerator.createAndPersistWeighing(entityManager);
        weighingService.deleteById(weighing.getId());
        Weighing found = entityManager.find(Weighing.class, weighing.getId());

        assertNull(found);
    }
}