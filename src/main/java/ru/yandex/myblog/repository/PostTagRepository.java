package ru.yandex.myblog.repository;

import ru.yandex.myblog.model.PostTag;

import java.util.List;

public interface PostTagRepository {

    void saveAll(List<PostTag> postTags);

    List<PostTag> findAllByPostId(Long postId);

    void removeAllByPostId(Long postId);
}
