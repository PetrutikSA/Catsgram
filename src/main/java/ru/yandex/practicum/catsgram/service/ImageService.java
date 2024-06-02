package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

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
public class ImageService {
    private final Map<Long, Image> images = new HashMap<>();
    @Value("${images.path}")
    private String imageDirectory;
    private final PostService postService;

    public List<Image> getPostImages (Long postId) {
        return images.values().stream()
                .filter(image -> image.getPostId() == postId)
                .toList();
    }

    public List<Image> addPostImages(Long postId, List<MultipartFile> files) {
        return files.stream().map(file -> saveImage(postId, file)).toList();
    }

    private Path saveToFile (MultipartFile file, Post post) {
        try {
            // формирование уникального названия файла на основе текущего времени и расширения оригинального файла
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            // формирование пути для сохранения файла с учётом идентификаторов автора и поста
            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), post.getId().toString());
            Path filePath = uploadPath.resolve(uniqueFileName);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image saveImage(Long postId, MultipartFile file) {
        Post post = postService.findById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));
        Path filePath = saveToFile(file, post);
        Image image = new Image();
        image.setId(getNextId());
        image.setPostId(postId);
        image.setOriginalFileName(file.getOriginalFilename());
        image.setFilePath(filePath.toString());
        images.put(image.getId(), image);
        return image;
    }

    private long getNextId() {
        long currentMaxId = images.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public ImageData getImagedata (Long imageId) {
        if (images.containsKey(imageId)) {
            Image image = images.get(imageId);
            return new ImageData(loadFile(image), image.getOriginalFileName());
        } else {
            throw new NotFoundException("Изображение с id = " + imageId + " не найдено");
        }
    }
    private byte[] loadFile(Image image) {
        Path path = Path.of(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }
}
