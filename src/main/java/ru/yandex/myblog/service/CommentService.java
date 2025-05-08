package ru.yandex.myblog.service;

import ru.yandex.myblog.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentService {

    void addComment(Long postId, String comment);

    void deleteComment(Long postId, Long commentId);

    void updateComment(Long postId, Long commentId, String message);

    List<Comment> getAllByPostId(Long postId);

    List<Comment> getAllByPostIds(Set<Long> postIds);
}
