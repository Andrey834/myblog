package ru.practicum.myblog5.mapper;

import ru.practicum.myblog5.dto.CommentDto;
import ru.practicum.myblog5.enums.DateTimeFormat;
import ru.practicum.myblog5.model.Comment;

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
