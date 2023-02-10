package ru.te4rus.auditor.service;

import io.jsonwebtoken.Claims;
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtUtils {
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstname", String.class));
        jwtInfoToken.setLastname(claims.get("lastname", String.class));
        jwtInfoToken.setLogin(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<ERole> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(ERole::valueOf)
                .collect(Collectors.toSet());
    }
}
