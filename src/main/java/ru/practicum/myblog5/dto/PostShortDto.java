package ru.practicum.myblog5.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PostShortDto(
        Long id,
        String title,
        String preview,
        String image,
        String created,
        long likes,
        List<String> tags,
        int countComments
) {
}

