package ru.practicum.myblog5.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.myblog5.model.PostTag;
import ru.practicum.myblog5.model.Tag;
import ru.practicum.myblog5.repository.PostTagRepository;
import ru.practicum.myblog5.repository.TagRepository;
import ru.practicum.myblog5.service.TagService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public TagServiceImpl(TagRepository tagRepository, PostTagRepository postTagRepository) {
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    @Override
    public List<Tag> getAllByPostId(Long postId) {
        return tagRepository.findAllByPostId(postId);
    }

    @Override
    public Map<Long, List<Tag>> getAllByPostIds(List<Long> postIds) {
        List<PostTag> postTags = postTagRepository.findAllByPostIds(postIds);
        Map<Long, List<Tag>> map = new HashMap<>();

        if (!postTags.isEmpty()) {
            List<Long> tagIds = postTags.stream().map(PostTag::getTagId).toList();
            List<Tag> tags = tagRepository.findAllByIds(tagIds);

            postTags.forEach(postTag -> {
                map.putIfAbsent(postTag.getPostId(), new ArrayList<>());

                List<Tag> currentTags = tags.stream()
                        .filter(tag -> tag.getId().equals(postTag.getTagId()))
                        .toList();

                map.get(postTag.getPostId()).addAll(currentTags);
            });
        }

        return map;
    }

    @Override
    public void saveAll(Long postId, List<Tag> tags) {
        List<String> titles = tags.stream().map(Tag::getTitle).toList();
        List<Tag> existingTags = tagRepository.findAllByTitle(titles);

        List<Tag> newTags = tags.stream().filter(tag -> !existingTags.contains(tag)).toList();
        Iterable<Tag> savedTags = tagRepository.saveAll(newTags);
        existingTags.addAll((Collection<? extends Tag>) savedTags);

        List<PostTag> postTags = new ArrayList<>();
        existingTags.forEach(tag ->
                postTags.add(PostTag.builder()
                        .tagId(tag.getId())
                        .postId(postId)
                        .build()
                )
        );

        postTagRepository.saveAll(postTags);
    }

    @Override
    public void deletePostTagsByPostId(Long postId) {
        postTagRepository.deleteByPostId(postId);
    }
}
