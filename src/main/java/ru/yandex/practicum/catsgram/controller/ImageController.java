package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/posts/{postId}/images")
    public List<Image> getPostImages(@PathVariable Long postId) {
        return imageService.getPostImages(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<Image> addPostImages(@PathVariable Long postId, @RequestParam("image") List<MultipartFile> images) {
        return imageService.addPostImages(postId, images);
    }

    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long imageId) {
        ImageData imageData = imageService.getImagedata(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(imageData.getName())
                .build());
        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }
}
