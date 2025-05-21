package ru.practicum.myblog5.dto;

import lombok.Builder;

@Builder
public record TagDto(
        Long id,
        String title
) {
}
