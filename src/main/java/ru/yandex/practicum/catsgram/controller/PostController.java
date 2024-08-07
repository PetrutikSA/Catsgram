package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "1") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "asc") String sort) {
        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало списка не может быть меньше нуля");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (!sort.equals("asc") && !sort.equals("desc")) {
            throw new ParameterNotValidException("sort", "Некорректный параметр сортировки. Должен быть либо asc, либо desc");
        }
        return postService.findAll(from, size, sort);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/{postId}")
    public Optional<Post> findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam long authorId, @RequestParam LocalDate date) {
        return postService.searchPosts(authorId, date);
    }
}
