package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Storage;
import ru.te4rus.auditor.dto.revision.RevisionCreateDto;
import ru.te4rus.auditor.dto.revision.RevisionDto;
import ru.te4rus.auditor.dto.revision.RevisionMapper;
import ru.te4rus.auditor.dto.revision.RevisionUpdateDto;
import ru.te4rus.auditor.service.AccessChecker;
import ru.te4rus.auditor.service.AuthService;
import ru.te4rus.auditor.service.RevisionService;
import ru.te4rus.auditor.service.StorageService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevisionApiManager {

    private final RevisionService revisionService;
    private final StorageService storageService;
    private final AuthService authService;

    public RevisionDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь с login:%s ищет ревизию по id:%s", authInfo.getPrincipal()), id);
        Revision revision = revisionService.findById(id);
        AccessChecker.check(revision, authInfo);

        return RevisionMapper.toRevisionDto(revision);
    }

    public List<RevisionDto> findByStorageId(@NonNull Long storageId) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String
                .format("Пользователь с login: %s ищет ревизии по storageId: %s", authInfo.getPrincipal(), storageId)
        );
        Storage storage = storageService.findById(storageId);
        AccessChecker.check(storage, authInfo);

        return revisionService.findByStorageId(storageId).stream()
                .map(RevisionMapper::toRevisionDto)
                .toList();
    }

    public RevisionDto addRevision(@NonNull RevisionCreateDto createDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь с login: %s создает ревизию: %s", authInfo.getPrincipal(), createDto));
        Storage storage = storageService.findById(createDto.getStorageId());
        AccessChecker.check(storage, authInfo);
        Revision newRevision = RevisionMapper.toRevision(createDto);
        newRevision.setStorage(storage);

        return RevisionMapper.toRevisionDto(revisionService.addRevision(newRevision));
    }

    public RevisionDto updateRevision(@NonNull RevisionUpdateDto updateDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(
                String.format("Пользователь с login: %s обновляет ревизию на %s", authInfo.getPrincipal(), updateDto)
        );
        Revision revision = revisionService.findById(updateDto.getId());
        AccessChecker.check(revision, authInfo);
        Revision updatedRevision = RevisionMapper.toRevision(updateDto);

        return RevisionMapper.toRevisionDto(revisionService.updateRevision(updatedRevision));

    }

    public void deleteById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь с login: %s удаляет ревизию по id:%s", authInfo.getPrincipal(), id));
        Revision revision = revisionService.findById(id);
        AccessChecker.check(revision, authInfo);

        revisionService.deleteById(id);
    }


}
