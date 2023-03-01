package ru.te4rus.auditor.service;

import lombok.NonNull;
import org.springframework.security.access.AccessDeniedException;
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.Revision;
import ru.te4rus.auditor.domain.Storage;

public class AccessChecker {

    public static void check(@NonNull Revision revision, @NonNull JwtAuthentication authInfo) {
        String revisionUserLogin = revision.getStorage().getUser().getLogin();
        checkAccess(authInfo, revisionUserLogin);
    }

    public static void check(@NonNull Storage storage, @NonNull JwtAuthentication authnfo) {
        String storageUserLogin = storage.getUser().getLogin();
        checkAccess(authnfo, storageUserLogin);
    }

    private static void checkAccess(@NonNull JwtAuthentication authInfo, @NonNull String userLogin) {
        String currentUserLogin = authInfo.getPrincipal();

        if (!currentUserLogin.equals(userLogin) && !authInfo.getRoles().contains(ERole.ADMIN)) {
            throw new AccessDeniedException(
                    String.format("Пользователь %s не иожет изменять информацию о складах другого пользователя",
                            currentUserLogin));
        }
    }

}
