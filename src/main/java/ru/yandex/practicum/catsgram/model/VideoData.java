package ru.yandex.practicum.catsgram.model;

import lombok.Data;

@Data
public class VideoData {
    private final String name;
    private final byte[] data;
}
