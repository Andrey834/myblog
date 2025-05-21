package ru.practicum.myblog5.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PostDto(
        Long id,
        String title,
        String[] textParts,
        String image,
        String created,
        Long likes,
        List<TagDto> tags,
        List<CommentDto> comments
) {
}
