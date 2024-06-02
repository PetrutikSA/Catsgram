package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.Video;
import ru.yandex.practicum.catsgram.model.VideoData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final Map<Long, Video> videos = new HashMap<>();
    private final PostService postService;
    @Value("${videos.path}")
    private String videosDirectory;

    public List<Video> uploadVideos(Long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> upploadVideo(postId, file))
                .toList();
    }

    private Video upploadVideo(Long postId, MultipartFile file) {
        Post post = postService.findById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));
        Path filePath = saveToFile(post, file);
        Video video = new Video();
        video.setId(getNextId());
        video.setFilePath(filePath.toString());
        video.setOriginalName(file.getOriginalFilename());
        video.setPostId(postId);
        videos.put(video.getId(), video);
        return video;
    }

    private Path saveToFile(Post post, MultipartFile file) {
        try {
            // формирование уникального названия файла на основе текущего времени и расширения оригинального файла
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            Path pathToUpload = Paths.get(videosDirectory,
                    String.valueOf(post.getAuthorId()), String.valueOf(post.getId()));
            if (!Files.exists(pathToUpload)) {
                Files.createDirectories(pathToUpload);
            }
            Path filePath = pathToUpload.resolve(uniqueFileName);
            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long getNextId() {
        long currentMaxId = videos.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public VideoData downloadVideo(Long videoId) {
        if (videos.containsKey(videoId)) {
            Video video = videos.get(videoId);
            return new VideoData(video.getOriginalName(), loadFile(video));
        } else {
            throw new NotFoundException("Видео с id = " + videos + " не найдено");
        }
    }

    private byte[] loadFile (Video video) {
        Path path = Path.of(video.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Файл не найден. Id: " + video.getId()
                        + ", name: " + video.getOriginalName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + video.getId()
                    + ", name: " + video.getOriginalName());
        }
    }
}
