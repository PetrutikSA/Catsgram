package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class Video {
    private Long id;
    private Long postId;
    private String originalName;
    private String filePath;
}
