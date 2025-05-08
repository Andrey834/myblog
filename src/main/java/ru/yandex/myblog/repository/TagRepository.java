package ru.yandex.myblog.repository;

import ru.yandex.myblog.model.Tag;

import java.util.List;
import java.util.Map;

public interface TagRepository {

    void saveAll(List<Tag> tags);

    List<Tag> findAllByTitle(List<String> tagTitles);

    Map<Long, List<Tag>> findAllByPostIds(List<Long> postIds);

    void delete(Long tagId);
}
