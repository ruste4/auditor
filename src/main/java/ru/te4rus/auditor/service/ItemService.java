package ru.te4rus.auditor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.te4rus.auditor.domain.*;
import ru.te4rus.auditor.exception.ItemNotFoundException;
import ru.te4rus.auditor.repository.ItemRepository;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Найти по id
     *
     * @return возвращает продукт
     * @throws AccessDeniedException если текущий пользователь не владелец продукта или не админ
     * @throws ItemNotFoundException если продукт не найден по id
     */
    public Item findById(@NonNull Long id, @NonNull JwtAuthentication authInfo) {
        Item item = findById(id);
        User user = item.getStorage().getUser();
        checkAccess(user, authInfo);

        return item;
    }

    /**
     * Найти все по определенному складу
     *
     * @return список продуктов
     * @throws AccessDeniedException если текущий пользователь не владелец склада или не админ
     */
    public List<Item> findByStorage(@NonNull Storage storage, @NonNull JwtAuthentication authInfo) {
        checkAccess(storage.getUser(), authInfo);

        return itemRepository.findAllByStorage(storage);
    }

    /**
     * Обновить предмет
     *
     * @return обновленный предмет
     * @throws AccessDeniedException если текущий пользователь не владелец продукта или не админ
     * @throws ItemNotFoundException если продукт не найден по id
     */
    public Item updateItem(@NonNull Item item, @NonNull JwtAuthentication authInfo) {
        Item itemInDB = findById(item.getId());
        User user = itemInDB.getStorage().getUser();
        checkAccess(user, authInfo);

        if (item.getName() != null) {
            itemInDB.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemInDB.setDescription(item.getDescription());
        }

        itemRepository.flush();

        return itemInDB;
    }

    /**
     * Удалить по id
     *
     * @throws AccessDeniedException если текущий пользователь не владелец продукта или не админ
     * @throws ItemNotFoundException если продукт не найден по id
     */
    public void deleteById(@NonNull Long id, @NonNull JwtAuthentication authInfo) {
        Item item = findById(id);
        User user = item.getStorage().getUser();
        checkAccess(user, authInfo);

        itemRepository.deleteById(id);
    }

    private Item findById(@NonNull Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Продукт с id:%s не найден", id)));
    }

    private void checkAccess(@NonNull User user, @NonNull JwtAuthentication authInfo) {
        String currentUserLogin = authInfo.getLogin();
        String itemOwnerLogin = user.getLogin();

        if (!currentUserLogin.equals(itemOwnerLogin) && !authInfo.getRoles().contains(ERole.ADMIN)) {
            throw new AccessDeniedException(
                    String.format("У пользователя %s нет доступа к продукту", currentUserLogin)
            );
        }
    }
}
