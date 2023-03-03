package ru.te4rus.auditor.service;

import lombok.NonNull;
import org.springframework.security.access.AccessDeniedException;
import ru.te4rus.auditor.domain.*;

public class AccessChecker {

    public static void check(@NonNull Item item, @NonNull JwtAuthentication authInfo) {
        String itemUserLogin = item.getStorage().getUser().getLogin();
        checkAccess(authInfo, itemUserLogin);
    }

    public static void check(@NonNull Revision revision, @NonNull JwtAuthentication authInfo) {
        String revisionUserLogin = revision.getStorage().getUser().getLogin();
        checkAccess(authInfo, revisionUserLogin);
    }

    public static void check(@NonNull Storage storage, @NonNull JwtAuthentication authInfo) {
        String storageUserLogin = storage.getUser().getLogin();
        checkAccess(authInfo, storageUserLogin);
    }

    public static void check(@NonNull User user, @NonNull JwtAuthentication authInfo) {
        checkAccess(authInfo, user.getLogin());
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
