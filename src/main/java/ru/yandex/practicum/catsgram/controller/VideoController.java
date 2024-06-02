package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Video;
import ru.yandex.practicum.catsgram.model.VideoData;
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

    @GetMapping(value = "/videos/{videoId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadVideo (@PathVariable Long videoId) {
        VideoData videoData = videoService.downloadVideo(videoId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(videoData.getName())
                .build());
        return new ResponseEntity<>(videoData.getData(), headers, HttpStatus.OK);
    }
}
