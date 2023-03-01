package ru.te4rus.auditor.dto.revision;

import lombok.NonNull;
import ru.te4rus.auditor.domain.Revision;

public class RevisionMapper {
    public static RevisionDto toRevisionDto(@NonNull Revision revision) {
        RevisionDto.Storage storage = new RevisionDto.Storage();
        storage.setId(revision.getStorage().getId());
        storage.setUserId(revision.getStorage().getUser().getId());

        RevisionDto revisionDto = new RevisionDto();
        revisionDto.setId(revision.getId());
        revisionDto.setDate(revision.getDate());
        revisionDto.setStorage(storage);

        return revisionDto;
    }

    public static Revision toRevision(@NonNull RevisionCreateDto createDto) {
        Revision revision = new Revision();
        revision.setDate(createDto.getDate());

        return revision;
    }

    public static Revision toRevision(@NonNull RevisionUpdateDto updateDto) {
        Revision revision = new Revision();
        revision.setId(updateDto.getId());
        revision.setDate(updateDto.getDate());

        return revision;
    }
}
