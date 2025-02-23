package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Добавляем фильм: {}", film);
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            throwValidationException("Название не может быть пустым");
        }
        validateFilm(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("Фильм: {} добавлен", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Обновляем фильм: {}", newFilm);

        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throwValidationException("Id должен быть указан");
        }
        validateFilm(newFilm);

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            // если фильм найден, обновляем его данные
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм: {} обновлен", oldFilm);
            return oldFilm;
        } else {
            log.error("Фильм с id = {} не найден", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }
    }

    // вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // вспомогательный метод валидации фильма
    private void validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            throwValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throwValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throwValidationException("Продолжительность должна быть положительным числом");
        }
    }

    // вспомогательный метод обработки исключений
    private void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }
}