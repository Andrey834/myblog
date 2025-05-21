package ru.practicum.myblog5.service;

public interface CommentService {

    void create(Long postId, String message);

    void update(Long postId, Long commentId, String message);

    void delete(Long postId, Long commentId);
}
