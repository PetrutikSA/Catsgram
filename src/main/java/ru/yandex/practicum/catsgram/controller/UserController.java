package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addNewUser (@RequestBody User user) {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validateUsername(user.getUsername());
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        Long id = user.getId();
        if (id == null || id == 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = users.get(id);
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        validatePassword(password);
        if (email != null && !email.isBlank()) {
            if (!oldUser.getEmail().equals(email)) {
                validateEmail(email);
                oldUser.setEmail(email);
            }
        }
        oldUser.setUsername(username);
        oldUser.setPassword(password);
        return user;
    }

    private long getNextId () {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateEmail (String email) {
        final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if (email == null || email.isBlank()) {
            throw new ConditionsNotMetException("E-mail не может быть пустым");
        } else if (isEmailAlreadyRegistered(email)) {
            throw new DuplicatedDataException("Этот e-mail уже используется");
        } else if(!emailPattern.matcher(email).matches()) {
            throw new ConditionsNotMetException("E-mail введен некорректный");
        }
    }

    private void validatePassword (String password) {
        if (password == null || password.isBlank()) {
            throw new ConditionsNotMetException("Password не может быть пустым");
        } else if (password.length() < 6) {
            throw new ConditionsNotMetException("Password должен быть не менее 6 символов");
        }
    }

    private void validateUsername (String username) {
        if (username == null || username.isBlank()) {
            throw new ConditionsNotMetException("Username не может быть пустым");
        }
    }

    private boolean isEmailAlreadyRegistered(String email) {
        Optional<String> emailInDB = users.values()
                .stream()
                .map(User::getEmail)
                .filter(email::equals)
                .findAny();
        return emailInDB.isPresent();
    }
}
