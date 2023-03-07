package ru.te4rus.auditor.api.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.te4rus.auditor.domain.Container;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Weighing;
import ru.te4rus.auditor.dto.weighing.WeighingCreateDto;
import ru.te4rus.auditor.dto.weighing.WeighingDto;
import ru.te4rus.auditor.dto.weighing.WeighingMapper;
import ru.te4rus.auditor.service.*;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeighingApiManager {

    private final WeighingService weighingService;
    private final RevisionService revisionService;
    private final ContainerService containerService;
    private final AuthService authService;

    public WeighingDto findById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s ищет взвешивание по id:%s", authInfo.getPrincipal(), id));
        Weighing weighing = weighingService.findById(id);
        AccessChecker.check(weighing.getRevision(), authInfo);

        return WeighingMapper.toWeighingDto(weighing);
    }

    public WeighingDto add(@NonNull WeighingCreateDto createDto) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s добавляет взвешивание %s", authInfo.getPrincipal(), createDto));
        Revision revision = revisionService.findById(createDto.getRevisionId());
        AccessChecker.check(revision, authInfo);
        Container container = containerService.findById(createDto.getContainerId());
        Weighing weighing = WeighingMapper.toWeighing(createDto);
        weighing.setRevision(revision);
        weighing.setContainer(container);

        return WeighingMapper.toWeighingDto(weighingService.add(weighing));
    }

    public List<WeighingDto> findByRevision(@NonNull Long revisionId) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format(
                "Пользователь %s ищет взвешивания по ревизии с id:%s", authInfo.getPrincipal(), revisionId)
        );
        Revision revision = revisionService.findById(revisionId);
        AccessChecker.check(revision, authInfo);

        return weighingService.findByRevision(revision).stream()
                .map(WeighingMapper::toWeighingDto)
                .toList();
    }

    public void deleteById(@NonNull Long id) {
        JwtAuthentication authInfo = authService.getAuthentication();
        log.debug(String.format("Пользователь %s удаляет взвешивание по id:%s", authInfo.getPrincipal(), id));
        Weighing weighing = weighingService.findById(id);
        AccessChecker.check(weighing.getRevision(), authInfo);
        weighingService.deleteById(id);
    }

}
