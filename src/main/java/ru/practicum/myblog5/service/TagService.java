package ru.practicum.myblog5.service;

import ru.practicum.myblog5.model.Tag;

import java.util.List;
import java.util.Map;

public interface TagService {

    List<Tag> getAllByPostId(Long postId);

    Map<Long, List<Tag>> getAllByPostIds(List<Long> postIds);

    void saveAll(Long postId, List<Tag> tags);

    void deletePostTagsByPostId(Long postId);
}
