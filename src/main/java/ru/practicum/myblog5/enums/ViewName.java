package ru.practicum.myblog5.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViewName {
    POSTS("posts"),
    PUBLISH("publish"),
    POST("post"),;

    private final String value;
}
