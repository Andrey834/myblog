package ru.yandex.myblog.dto;

import lombok.Builder;

@Builder
public record TagDto(
        Long id,
        String title
) {
}
