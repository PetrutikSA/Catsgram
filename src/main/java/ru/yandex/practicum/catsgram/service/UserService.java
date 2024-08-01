package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.UserDTO;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDTO addNewUser(User user) {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validateUsername(user.getUsername());
        user.setRegistrationDate(Instant.now());
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDTO updateUser(long userId, User request) {
        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    private void validateEmail(String email) {
        final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if (email == null || email.isBlank()) {
            throw new ConditionsNotMetException("E-mail не может быть пустым");
        } else if (isEmailAlreadyRegistered(email)) {
            throw new DuplicatedDataException("Этот e-mail уже используется");
        } else if (!emailPattern.matcher(email).matches()) {
            throw new ConditionsNotMetException("E-mail введен некорректный");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ConditionsNotMetException("Password не может быть пустым");
        } else if (password.length() < 6) {
            throw new ConditionsNotMetException("Password должен быть не менее 6 символов");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ConditionsNotMetException("Username не может быть пустым");
        }
    }

    private boolean isEmailAlreadyRegistered(String email) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(email);
        return alreadyExistUser.isPresent();
    }

    public UserDTO getUserWithId(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));

    }
}
