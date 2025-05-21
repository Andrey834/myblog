package ru.practicum.myblog5.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public enum DateTimeFormat {
    FOR_VIEW_FULL(DateTimeFormatter.ofPattern("dd MMMM yyy HH:mm:ss"));

    private final DateTimeFormatter formatter;
}

