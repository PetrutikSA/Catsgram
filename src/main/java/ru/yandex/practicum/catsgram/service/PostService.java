package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dto.UserDTO;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService; // @Autowired применяется автоматически, тк final

    public Collection<Post> findAll(int from, int size, String sort) {
        Comparator<Instant> sortOrder = (sort.equals("desc")) ? Comparator.reverseOrder() : Comparator.naturalOrder();
        return posts.values().stream()
                .sorted(Comparator.comparing(Post::getPostDate, sortOrder))
                .skip((from - 1))
                .limit(size)
                .toList();
    }

    public Post create(Post post) {
        // проверяем выполнение необходимых условий
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        //проверяем что у поста передан существующий пользователь
        //TODO
        UserDTO user = userService.getUserWithId(post.getAuthorId());

        // формируем дополнительные данные
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        posts.put(post.getId(), post);
        return post;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Post update(Post newPost) {
        // проверяем необходимые условия
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Optional<Post> findById(Long postId) {
        return posts.values().stream()
                .filter(x -> Objects.equals(x.getId(), postId))
                .findFirst();
    }

    public List<Post> searchPosts(long authorId, LocalDate date) {
        return posts.values().stream()
                .filter(post -> post.getAuthorId() == authorId)
                .filter(post -> LocalDate.ofInstant(post.getPostDate(), ZoneId.systemDefault()).equals(date))
                .toList();
    }
}
