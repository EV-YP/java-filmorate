package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validUser = new User();
        validUser.setId(1L);
        validUser.setLogin("testLogin");
        validUser.setEmail("test@example.com");
        validUser.setName("Test User");
        validUser.setPassword("password123");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUser_validUser_shouldReturnUser() {
        User createdUser = userController.create(validUser);

        assertNotNull(createdUser);
        assertEquals(validUser.getEmail(), createdUser.getEmail());
    }

    @Test
    void createUser_invalidUser_missingEmail_shouldThrowValidationException() {
        validUser.setEmail(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(validUser));

        assertEquals("Email не может быть пустым и должен содержать символ @", exception.getMessage());
    }

    @Test
    void createUser_invalidUser_invalidEmail_shouldThrowValidationException() {
        validUser.setEmail("invalid-email");

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(validUser));

        assertEquals("Email не может быть пустым и должен содержать символ @", exception.getMessage());
    }

    @Test
    void createUser_invalidUser_missingLogin_shouldThrowValidationException() {
        validUser.setLogin(" ");

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(validUser));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void createUser_invalidUser_birthdayInFuture_shouldThrowValidationException() {
        validUser.setBirthday(LocalDate.of(2026, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(validUser));

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void updateUser_validUser_shouldUpdateUser() {
        userController.create(validUser);

        User updatedUser = userController.update(validUser);

        assertNotNull(updatedUser);
        assertEquals(validUser.getLogin(), updatedUser.getLogin());
    }

    @Test
    void updateUser_invalidUser_missingId_shouldThrowValidationException() {
        validUser.setId(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.update(validUser));

        assertEquals("Id должен быть указан", exception.getMessage());
    }
}