package ru.practicum.myblog5.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.myblog5.model.PostTag;
import ru.practicum.myblog5.model.Tag;
import ru.practicum.myblog5.repository.PostRepository;
import ru.practicum.myblog5.repository.PostTagRepository;
import ru.practicum.myblog5.repository.TagRepository;
import ru.practicum.myblog5.service.impl.TagServiceImpl;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TagServiceImpl.class})
class TagServiceImplTest {
    @Autowired
    private TagService tagService;
    @MockitoBean
    private TagRepository tagRepository;
    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private PostTagRepository postTagRepository;

    private final Long postId = 1L;

    @Test
    void getAllByPostId() {
        when(tagRepository.findAllByPostId(postId)).thenReturn(anyList());

        tagService.getAllByPostId(postId);

        verify(tagRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    void getAllByPostIds() {
        Tag tag1 = new Tag(2L, "tag1");
        List<PostTag> postTags = List.of(PostTag.builder()
                .id(1L)
                .postId(postId)
                .tagId(tag1.getId())
                .build()
        );

        when(postTagRepository.findAllByPostIds(List.of(postId))).thenReturn(postTags);
        when(tagRepository.findAllByIds(List.of(tag1.getId()))).thenReturn(List.of(tag1));

        Map<Long, List<Tag>> allByPostIds = tagService.getAllByPostIds(List.of(postId));

        assertNotNull(allByPostIds);
        assertEquals(1, allByPostIds.size());
        assertTrue(allByPostIds.containsKey(postId));
        assertTrue(allByPostIds.get(postId).contains(tag1));
        verify(tagRepository, times(1)).findAllByIds(List.of(tag1.getId()));
    }

    @Test
    void saveAll() {
        List<Tag> tags = List.of(
                Tag.builder().title("tag1").build(),
                Tag.builder().title("tag2").build(),
                Tag.builder().title("tag3").build()
        );

        tagService.saveAll(postId, tags);

        verify(tagRepository, times(1)).saveAll(tags);
        verify(tagRepository, times(1)).findAllByTitle(anyList());
        verify(postTagRepository, times(1)).saveAll(anyList());
    }

    @Test
    void deletePostTagsByPostId() {
        tagService.deletePostTagsByPostId(postId);

        verify(postTagRepository, times(1)).deleteByPostId(postId);
    }
}
