package ru.practicum.myblog5.mapper;

import ru.practicum.myblog5.dto.TagDto;
import ru.practicum.myblog5.model.Tag;

public class TagMapper {
    private TagMapper() {
    }

    public static TagDto toTagDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .build();
    }
}
