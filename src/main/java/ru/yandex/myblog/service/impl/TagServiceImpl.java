package ru.yandex.myblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.myblog.model.PostTag;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.service.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public TagServiceImpl(TagRepository tagRepository, PostTagRepository postTagRepository) {
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    @Override
    @Transactional
    public void saveAll(Long postId, List<Tag> tags) {
        tagRepository.saveAll(tags);
        savePostTags(postId, tags);
    }

    @Override
    public List<Tag> getAllByPostId(Long postId) {
        Map<Long, List<Tag>> tagsMap = tagRepository.findAllByPostIds(List.of(postId));
        return tagsMap.getOrDefault(postId, new ArrayList<>());
    }

    @Override
    public Map<Long, List<Tag>> getAllByPostIds(Set<Long> postIds) {
        return tagRepository.findAllByPostIds(postIds.stream().toList());
    }

    @Override
    public void deletePostTagsByPostId(Long postId) {
        postTagRepository.removeAllByPostId(postId);
    }

    private void savePostTags(Long postId, List<Tag> tags) {
        List<String> strTags = tags.stream().map(Tag::getTitle).toList();

        List<PostTag> postTags = tagRepository.findAllByTitle(strTags).stream()
                .map(t -> new PostTag(postId, t.getId()))
                .toList();

        postTagRepository.saveAll(postTags);
    }
}
