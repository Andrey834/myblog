package ru.practicum.myblog5.dto;

import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        String message,
        String created
) {
}
