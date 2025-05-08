package ru.yandex.myblog.enums;

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

