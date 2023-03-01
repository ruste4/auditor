package ru.te4rus.auditor.exception;

public class RevisionNotFoundException extends RuntimeException {
    public RevisionNotFoundException(String message) {
        super(message);
    }
}
