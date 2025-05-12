package ru.yandex.myblog.mapper;

import ru.yandex.myblog.dto.CommentDto;
import ru.yandex.myblog.enums.DateTimeFormat;
import ru.yandex.myblog.model.Comment;

public class CommentMapper {
    private CommentMapper() {
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .message(comment.getText())
                .created(comment.getCreated().format(DateTimeFormat.FOR_VIEW_FULL.getFormatter()))
                .build();

    }
}
