package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setId(1L);
        validFilm.setName("Test Film");
        validFilm.setDescription("This is a test film description.");
        validFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        validFilm.setDuration(120L);
    }

    @Test
    void createFilm_validFilm_shouldReturnFilm() {
        Film createdFilm = filmController.create(validFilm);

        assertNotNull(createdFilm);
        assertEquals(validFilm.getName(), createdFilm.getName());
    }

    @Test
    void createFilm_invalidFilm_missingName_shouldThrowValidationException() {
        validFilm.setName(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(validFilm));

        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void updateFilm_validFilm_shouldUpdateFilm() {
        filmController.create(validFilm);

        Film updatedFilm = filmController.update(validFilm);

        assertNotNull(updatedFilm);
        assertEquals(validFilm.getName(), updatedFilm.getName());
    }

    @Test
    void updateFilm_invalidFilm_missingId_shouldThrowValidationException() {
        validFilm.setId(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.update(validFilm));

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void validateFilm_invalidDescription_shouldThrowValidationException() {
        validFilm.setDescription("A".repeat(201));

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(validFilm));

        assertEquals("Максимальная длина описания - 200 символов", exception.getMessage());
    }

    @Test
    void validateFilm_invalidReleaseDate_shouldThrowValidationException() {
        validFilm.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(validFilm));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void validateFilm_invalidDuration_shouldThrowValidationException() {
        validFilm.setDuration(-1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(validFilm));

        assertEquals("Продолжительность должна быть положительным числом", exception.getMessage());
    }
}
