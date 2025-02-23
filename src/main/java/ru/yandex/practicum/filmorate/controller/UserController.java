package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


//Добавьте unit-тесты для всех методов
//Валидация должна работать на граничных условиях, не пропускать пустые или неверно заполненные поля.

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Добавляем пользователя: {}", user);
        // проверяем выполнение необходимых условий
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throwValidationException("Email не может быть пустым и должен содержать символ @");
        }
        validateUser(user);

        // формируем дополнительные данные
        user.setId(getNextId());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("Пользователь: {} добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Обновляем пользователя: {}", newUser);

        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throwValidationException("Id должен быть указан");
        }
        if (!newUser.getEmail().contains("@")) {
            throwValidationException("Email не может быть пустым и должен содержать символ @");
        }
        validateUser(newUser);

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            // если пользователь найден, обновляем его данные
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Пользователь: {} обновлен", oldUser);
            return oldUser;
        } else {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }
    }

    // вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // вспомогательный метод валидации фильма
    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throwValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throwValidationException("Дата рождения не может быть в будущем");
        }
    }

    // вспомогательный метод обработки исключений
    private void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }
}