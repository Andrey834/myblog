package ru.yandex.myblog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.myblog.configuration.ServiceConfig;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.service.impl.TagServiceImpl;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {ServiceConfig.class, TagServiceImpl.class})
@ActiveProfiles(profiles = "unit-test")
class TagServiceImplTest {
    @Autowired
    private TagService tagService;
    @MockitoBean
    private TagRepository tagRepository;
    @MockitoBean
    private PostRepository postRepository;
    @Autowired
    private PostTagRepository postTagRepository;
    private final Long postId = 1L;

    @Test
    void testSaveAll_whenInvoke_success() {
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
    void getAllByPostId() {
        when(tagRepository.findAllByPostIds(List.of(postId))).thenReturn(anyMap());

        tagService.getAllByPostId(postId);

        verify(tagRepository, times(1)).findAllByPostIds(List.of(postId));
    }

    @Test
    void getAllByPostIds() {
        when(tagRepository.findAllByPostIds(List.of(postId))).thenReturn(anyMap());

        tagService.getAllByPostIds(Set.of(postId));

        verify(tagRepository, times(1)).findAllByPostIds(List.of(postId));
    }

    @Test
    void deletePostTagsByPostId() {
        tagService.deletePostTagsByPostId(postId);

        verify(postTagRepository, times(1)).removeAllByPostId(postId);
    }
}
