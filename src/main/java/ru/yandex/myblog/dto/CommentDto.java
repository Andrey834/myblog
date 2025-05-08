package ru.yandex.myblog.dto;

import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        String message,
        String created
) {
}
