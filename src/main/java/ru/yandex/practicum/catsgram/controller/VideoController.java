package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Video;
import ru.yandex.practicum.catsgram.service.VideoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/posts/{postId}/videos")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Video> uploadVideo(@PathVariable Long postId, @RequestParam("video") List<MultipartFile> videos) {
        return videoService.uploadVideos(postId, videos);
    }
}
