package ru.te4rus.auditor.dto.weighing;

import lombok.NonNull;
import ru.te4rus.auditor.domain.Weighing;

public class WeighingMapper {

    public static WeighingDto toWeighingDto(@NonNull Weighing weighing) {
        WeighingDto weighingDto = new WeighingDto();
        weighingDto.setId(weighing.getId());
        weighingDto.setContainerId(weighing.getContainer().getId());
        weighingDto.setRevisionId(weighing.getRevision().getId());
        weighingDto.setWeight(weighing.getWeight());

        return weighingDto;
    }

    public static Weighing toWeighing(@NonNull WeighingCreateDto createDto) {
        Weighing weighing = new Weighing();
        weighing.setWeight(createDto.getWeight());

        return weighing;
    }

}
