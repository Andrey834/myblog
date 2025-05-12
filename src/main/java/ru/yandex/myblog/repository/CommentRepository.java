package ru.yandex.myblog.repository;

import ru.yandex.myblog.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepository {

    void save(Comment comment);

    void update(Comment comment);

    Optional<Comment> findById(Long id);

    void delete(Long postId, Long id);

    List<Comment> findAllByPostId(Long postId);

    List<Comment> findAllByPostIds(Set<Long> postIds);
}
