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
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.exception.RevisionNotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RevisionServiceTest {

    @Autowired
    private final RevisionService revisionService;

    @Autowired
    private final TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        entityManager.clear();
    }

    @Test
    public void addRevisionSuccess() {
        Revision createdRevision = revisionService.addRevision(EntityGenerator.createRevision(entityManager));
        Revision found = entityManager.find(Revision.class, createdRevision.getId());

        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(createdRevision.getDate(), found.getDate()),
                () -> assertEquals(createdRevision.getStorage(), found.getStorage())
        );
    }

    @Test
    public void findAllByStorageIdSuccess() {
        Storage storage1 = EntityGenerator.createAndPersistStorage(entityManager);
        Storage storage2 = EntityGenerator.createAndPersistStorage(entityManager);

        Revision firstRevisionForStorage1 = EntityGenerator.createAndPersistRevision(entityManager, storage1);
        Revision secondRevisionForStorage1 = EntityGenerator.createAndPersistRevision(entityManager, storage1);
        Revision firstRevisionForStorage2 = EntityGenerator.createAndPersistRevision(entityManager, storage2);

        List<Revision> revisionsForStorage1 = List.of(firstRevisionForStorage1, secondRevisionForStorage1);
        List<Revision> revisionsForStorage2 = List.of(firstRevisionForStorage2);

        List<Revision> foundRevisionsByStorage1Id = revisionService.findByStorageId(storage1.getId());
        List<Revision> foundRevisionsByStorage2Id = revisionService.findByStorageId(storage2.getId());

        assertAll(
                () -> assertEquals(foundRevisionsByStorage1Id, revisionsForStorage1),
                () -> assertEquals(foundRevisionsByStorage2Id, revisionsForStorage2)
        );
    }

    @Test
    public void findByIdSuccess() {
        Revision revision = EntityGenerator.createAndPersistRevision(entityManager);
        Revision found = revisionService.findById(revision.getId());

        assertEquals(revision, found);
    }

    @Test
    public void findByIdFail() {
        assertThrows(RevisionNotFoundException.class, () -> revisionService.findById(Long.MAX_VALUE));
    }

    @Test
    public void updateRevisionSuccess() {
        Revision revision = EntityGenerator.createAndPersistRevision(entityManager);
        LocalDate updatedDate = LocalDate.MAX;
        Revision updatedRevision = new Revision();
        updatedRevision.setId(revision.getId());
        updatedRevision.setDate(updatedDate);
        revisionService.updateRevision(updatedRevision);
        Revision found = entityManager.find(Revision.class, revision.getId());

        assertEquals(updatedDate, found.getDate());
    }

    @Test
    public void deleteIdSuccess() {
        Revision revision = EntityGenerator.createAndPersistRevision(entityManager);
        revisionService.deleteById(revision.getId());

        assertNull(entityManager.find(Revision.class, revision.getId()));
    }

}