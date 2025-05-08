package ru.yandex.myblog.repository;

import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.model.Post;

import java.util.Map;
import java.util.Optional;

public interface PostRepository {

    Long save(Post post);

    void update(Post post);

    Optional<Post> findById(Long id);

    Map<Long, Post> findAll(Paging paging);

    void delete(Long id);

    void changeLikes(Long postId, int changer);

    Long quantityByTag(String searchTag);
}
