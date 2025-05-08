package ru.yandex.myblog.service;

import ru.yandex.myblog.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TagService {

    void saveAll(Long postId, List<Tag> strTags);

    List<Tag> getAllByPostId(Long postId);

    Map<Long, List<Tag>> getAllByPostIds(Set<Long> postIds);

    void deletePostTagsByPostId(Long postId);
}
