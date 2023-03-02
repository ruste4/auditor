package ru.te4rus.auditor.dto.storage;

import lombok.Data;

@Data
public class StorageDto {

    private Long id;

    private String name;

    private String address;

    private User user;

    @Data
    public static class User {
        private Long id;

        private String login;
    }

}
