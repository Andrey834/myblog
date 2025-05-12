package ru.yandex.myblog.mapper;

import ru.yandex.myblog.dto.TagDto;
import ru.yandex.myblog.model.Tag;

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
