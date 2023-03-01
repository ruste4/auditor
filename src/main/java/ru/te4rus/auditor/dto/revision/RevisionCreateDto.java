package ru.te4rus.auditor.dto.revision;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RevisionCreateDto {
    @NotNull
    private LocalDate date;

    @NotNull
    private Long storageId;
}
