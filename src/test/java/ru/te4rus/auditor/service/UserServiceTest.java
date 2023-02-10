package ru.te4rus.auditor.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.ERole;
import ru.te4rus.auditor.domain.JwtAuthentication;
import ru.te4rus.auditor.domain.User;
import ru.te4rus.auditor.exception.UserNotFoundException;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    @Autowired
    private final UserService userService;

    @Autowired
    private final TestEntityManager testEntityManager;

    private static AtomicLong userIdHolder;

    private final Supplier<User> userSupplier = () -> {
        User user = new User();
        long userId = userIdHolder.getAndIncrement();

        user.setLogin("login-" + userId);
        user.setFirstname("firstname-" + userId);
        user.setLastname("lastname-" + userId);

        return user;
    };

    @BeforeAll
    public static void init() {
        userIdHolder = new AtomicLong();
    }

    @BeforeEach
    void setUp() {
        testEntityManager.clear();
    }

    @Test
    public void addUserSuccess() {
        User user = userService.add(userSupplier.get());
        User userInDB = testEntityManager.find(User.class, user.getId());

        assertAll(
                () -> assertEquals(user.getLogin(), userInDB.getLogin()),
                () -> assertEquals(user.getFirstname(), userInDB.getFirstname()),
                () -> assertEquals(user.getLastname(), userInDB.getLastname())
        );
    }

    @Test
    public void addUserFailLoginAlreadyExist() {
        User user = userSupplier.get();
        User userWithDuplicateLogin = userSupplier.get();
        userWithDuplicateLogin.setLogin(user.getLogin());

        testEntityManager.persist(user);

        assertThrows(DataIntegrityViolationException.class, () -> userService.add(userWithDuplicateLogin));
    }

    @Test
    void updateUserLoginSuccess() {
        User user = createAndPersistUser();
        User updatedUser = userSupplier.get();
        String updatedLogin = "UpdatedLogin";
        JwtAuthentication authInfo = generateAuthInfo(user.getLogin());

        updatedUser.setLogin(updatedLogin);
        updatedUser.setId(user.getId());

        userService.update(updatedUser, authInfo);

        assertEquals(testEntityManager.find(User.class, user.getId()).getLogin(), updatedLogin);
    }

    @Test
    void updateUserLoginFailLoginAlreadyExist() {
        User user1 = createAndPersistUser();
        User user2 = createAndPersistUser();
        User updatedUser2 = new User();
        JwtAuthentication authInfo = generateAuthInfo(user2.getLogin());

        updatedUser2.setId(user2.getId());
        updatedUser2.setLogin(user1.getLogin());

        assertThrows(DataIntegrityViolationException.class, () -> userService.update(updatedUser2, authInfo));

    }

    @Test
    void updateUserFailAccessDeniedAnotherUser() {
        User user = createAndPersistUser();
        User anotherUser = createAndPersistUser();
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setLastname("UpdatedLastName");
        JwtAuthentication autInfo = generateAuthInfo(anotherUser.getLogin());

        assertThrows(AccessDeniedException.class, () -> userService.update(updatedUser, autInfo));
    }

    @Test
    void updateUserSuccessWithAnotherUserIsAdmin() {
        User user = createAndPersistUser();
        User anotherUser = createAndPersistUser();
        String updatedFirstname = "UpdatedFirstName";
        User updatedUser = new User(user.getId(), null, updatedFirstname, null);
        JwtAuthentication authInfo = generateAuthInfo(anotherUser.getLogin());
        authInfo.setRoles(Collections.singleton(ERole.ADMIN));

        userService.update(updatedUser, authInfo);

        User found = testEntityManager.find(User.class, user.getId());

        assertEquals(found.getFirstname(), updatedFirstname);
    }

    @Test
    public void findUserByIdSuccess() {
        User user = createAndPersistUser();

        assertNotNull(userService.findById(user.getId()));
    }

    @Test
    public void findUserByIdFailAndThrowUserNotFound() {
        User user = createAndPersistUser();

        assertThrows(UserNotFoundException.class, () -> userService.findById(user.getId() + 1));
    }

    @Test
    public void findUserByLoginSuccess() {
        User user = createAndPersistUser();

        assertNotNull(userService.findByLogin(user.getLogin()));
    }

    @Test
    public void findUserByLoginFail() {
        User user = createAndPersistUser();

        assertThrows(UserNotFoundException.class, () -> userService.findByLogin(user.getLogin() + "test"));
    }

    @Test
    public void deleteUserByIdSuccess() {
        User user = createAndPersistUser();
        userService.deleteUserById(user.getId());

        assertNull(testEntityManager.find(User.class, user.getId()));

    }

    private User createAndPersistUser() {
        return testEntityManager.persist(userSupplier.get());
    }

    private JwtAuthentication generateAuthInfo(String login) {
        JwtAuthentication authInfo = new JwtAuthentication();
        authInfo.setLogin(login);
        authInfo.setRoles(Collections.singleton(ERole.USER));
        return authInfo;
    }
}