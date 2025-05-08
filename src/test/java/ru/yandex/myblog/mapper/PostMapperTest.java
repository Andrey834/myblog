package ru.yandex.myblog.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;
import ru.yandex.myblog.dto.TagDto;
import ru.yandex.myblog.enums.DateTimeFormat;
import ru.yandex.myblog.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {
    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .title("test")
                .text("Hello, World!")
                .image("image.jpg")
                .likes(0L)
                .tags(new ArrayList<>())
                .comments(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void requestToPost() {
        String imageName = "image.jpg";
        MockMultipartFile file = new MockMultipartFile(
                imageName,
                "test.jpg",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        RequestPost requestPost = new RequestPost(
                "New Post",
                "La la ha ha ha ha",
                "max low",
                file
        );

        Post post = PostMapper.requestToPost(requestPost, imageName);

        assertNotNull(post);
        assertEquals(Post.class, post.getClass());
        assertEquals(requestPost.getText(), post.getText());
        assertEquals(requestPost.getTitle(), post.getTitle());
        assertEquals(2, post.getTags().size());
        assertNotNull(post.getCreated());
        assertEquals(0, post.getLikes());
        assertEquals(imageName, post.getImage());
    }

    @Test
    void toRequestPost() {
        String[] texts = {"test1", "test2", "test3", "test4"};
        List<TagDto> tags = new ArrayList<>();
        tags.add(new TagDto(1L, "tag1"));
        tags.add(new TagDto(2L, "tag2"));
        tags.add(new TagDto(3L, "tag2"));

        PostDto postDto = PostDto.builder()
                .id(1L)
                .title("test")
                .textParts(texts)
                .tags(tags)
                .image("image.jpg")
                .created(LocalDateTime.now().format(DateTimeFormat.FOR_VIEW_FULL.getFormatter()))
                .likes(0L)
                .comments(new ArrayList<>())
                .build();

        RequestPost requestPost = PostMapper.toRequestPost(postDto);

        assertNotNull(requestPost);
        assertEquals(RequestPost.class, requestPost.getClass());
        assertEquals(postDto.title(), requestPost.getTitle());
        assertNotNull(requestPost.getText());
        assertNotNull(requestPost.getTags());
    }

    @Test
    void toPostDto() {
        PostDto postDto = PostMapper.toPostDto(post);

        assertNotNull(postDto);
        assertEquals(PostDto.class, postDto.getClass());
        assertEquals(post.getId(), postDto.id());
        assertEquals(post.getTitle(), postDto.title());
        assertEquals(post.getImage(), postDto.image());
        assertEquals(post.getLikes(), postDto.likes());
    }

    @Test
    void toPostShortDto() {
        PostShortDto postShortDto = PostMapper.toPostShortDto(post);

        assertNotNull(postShortDto);
        assertEquals(PostShortDto.class, postShortDto.getClass());
        assertEquals(post.getId(), postShortDto.id());
        assertEquals(post.getTitle(), postShortDto.title());
        assertEquals(post.getImage(), postShortDto.image());
        assertEquals(post.getLikes(), postShortDto.likes());
    }
}
