package ru.te4rus.auditor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.te4rus.auditor.service.AuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/test")
public class Controller {

    private final AuthService authService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<String> getTest() {

        log.info(authService.getAuthentication().toString());
        return ResponseEntity.ok("Запрос от " + authService.getAuthentication().getPrincipal());
    }

}
