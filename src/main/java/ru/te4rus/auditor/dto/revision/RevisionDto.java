package ru.te4rus.auditor.dto.revision;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RevisionDto {

    private Long id;

    private LocalDate date;

    private Storage storage;

    @Data
    public static class Storage {

        private Long id;

        private Long userId;

    }
}
