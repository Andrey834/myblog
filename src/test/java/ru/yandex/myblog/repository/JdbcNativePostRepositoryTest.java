package ru.yandex.myblog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.repository.impl.JdbcNativePostRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostRepository.class})
class JdbcNativePostRepositoryTest extends PostgresTestContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM post");
    }

    @Test
    void testSave_shouldAddPostToDatabase() {
        Paging paging = getPaging(1, 999, "");
        Map<Long, Post> actualPosts = postRepository.findAll(paging);
        int expectedSize = 0;
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        Post post = generatePost();
        Long actualId = postRepository.save(post);

        actualPosts = postRepository.findAll(paging);
        assertNotNull(actualId);

        expectedSize = 1;
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        Post actualPost = actualPosts.get(actualId);
        assertEquals(post.getTitle(), actualPost.getTitle(), "Wrong title");
        assertEquals(post.getText(), actualPost.getText(), "Wrong text");
        assertEquals(post.getImage(), actualPost.getImage(), "Wrong image");
        assertEquals(post.getCreated(), actualPost.getCreated(), "Wrong created");
        assertEquals(post.getLikes(), actualPost.getLikes(), "Wrong likes");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFieldsInPost")
    void testSave_whenInvalidPost_shouldThrowException(Post invalidPost) {
        assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(invalidPost));

        Paging paging = getPaging(1, 999, "");
        Map<Long, Post> actualPosts = postRepository.findAll(paging);
        int expectedSize = 0;
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");
    }

    @Test
    void update() {
        Paging paging = getPaging(1, 999, "");
        Post oldPost = generatePost();
        Long oldPostId = postRepository.save(oldPost);
        Map<Long, Post> actualPosts = postRepository.findAll(paging);
        int expectedSize = 1;
        assertTrue(actualPosts.containsKey(oldPostId), "Wrong post");
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        Post updatePost = generatePost();
        updatePost.setId(oldPostId);
        postRepository.update(updatePost);

        actualPosts = postRepository.findAll(paging);
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        Post actualPost = actualPosts.get(oldPostId);

        assertEquals(updatePost.getTitle(), actualPost.getTitle(), "Wrong title");
        assertEquals(updatePost.getText(), actualPost.getText(), "Wrong text");
        assertEquals(updatePost.getImage(), actualPost.getImage(), "Wrong image");
    }

    @Test
    void delete() {
        Paging paging = getPaging(1, 999, "");
        Post oldPost = generatePost();
        Long postId = postRepository.save(oldPost);
        Map<Long, Post> actualPosts = postRepository.findAll(paging);
        int expectedSize = 1;
        assertTrue(actualPosts.containsKey(postId), "Wrong post");
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        postRepository.delete(postId);

        actualPosts = postRepository.findAll(paging);
        expectedSize = 0;
        assertFalse(actualPosts.containsKey(postId), "Post must not exist");
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

    }

    @Test
    void changeLikes() {
        Paging paging = getPaging(1, 999, "");
        Post post = generatePost();
        Long postId = postRepository.save(post);

        Map<Long, Post> actualPosts = postRepository.findAll(paging);
        Post actualPost = actualPosts.get(postId);
        int expectedLikes = 0;

        assertEquals(expectedLikes, actualPost.getLikes(), "Wrong number of likes");

        postRepository.changeLikes(postId, 1);

        actualPosts = postRepository.findAll(paging);
        actualPost = actualPosts.get(postId);
        expectedLikes = 1;
        assertEquals(expectedLikes, actualPost.getLikes(), "Wrong number of likes");
    }

    @Test
    void findById() {
        Post post = generatePost();
        Long postId = postRepository.save(post);

        Optional<Post> optionalPost = postRepository.findById(postId);
        assertTrue(optionalPost.isPresent(), "Wrong post");

        Post actualPost = optionalPost.get();
        assertEquals(post.getTitle(), actualPost.getTitle(), "Wrong title");
        assertEquals(post.getText(), actualPost.getText(), "Wrong text");
        assertEquals(post.getImage(), actualPost.getImage(), "Wrong image");
        assertEquals(post.getCreated(), actualPost.getCreated(), "Wrong created");
        assertEquals(post.getLikes(), actualPost.getLikes(), "Wrong likes");
    }

    @Test
    void findAll() {
        int expectedSize = 100;
        Paging paging = getPaging(1, expectedSize, "");

        for (int i = 0; i < expectedSize; i++) {
            Post post = generatePost();
            postRepository.save(post);
        }

        Map<Long, Post> actualPosts = postRepository.findAll(paging);

        int actualSize = actualPosts.size();
        assertEquals(expectedSize, actualSize, "Wrong number of posts");
    }

    @Test
    void quantityByTag() {
        Post post1 = generatePost();
        Post post2 = generatePost();
        Post post3 = generatePost();

        Long postId1 = postRepository.save(post1);
        Long postId2 = postRepository.save(post2);
        Long postId3 = postRepository.save(post3);

        Paging paging = getPaging(1, 999, "");
        Map<Long, Post> actualPosts = postRepository.findAll(paging);

        int expectedSize = 3;
        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts");

        String testTag = "testTag";
        jdbcTemplate.update("INSERT INTO tag (title) VALUES (?)", testTag);
        Long tagId = jdbcTemplate.queryForObject("SELECT id FROM tag WHERE title = ?", Long.class, testTag);

        jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)", postId1, tagId);
        jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)", postId2, tagId);

        paging = getPaging(1, 999, testTag);

        actualPosts = postRepository.findAll(paging);
        expectedSize = 2;

        assertEquals(expectedSize, actualPosts.size(), "Wrong number of posts(must be 2)");
        assertTrue(actualPosts.containsKey(postId1), "Post with id 1 not found");
        assertTrue(actualPosts.containsKey(postId2), "Post with id 2 not found");
    }

    private Post generatePost() {
        String title = UUID.randomUUID().toString();
        String text = UUID.randomUUID().toString();
        String image = UUID.randomUUID().toString();
        LocalDateTime created = LocalDateTime.now();
        Long likes = 0L;

        return Post.builder()
                .title(title)
                .text(text)
                .image(image)
                .created(created)
                .likes(likes)
                .build();
    }

    private Paging getPaging(int page, int size, String searech) {
        return new Paging(page, size, searech, 0, new int[]{5, 10, 20, 50});
    }

    private static Stream<Post> provideInvalidFieldsInPost() {
        Post post1 = new Post(null, null, UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), LocalDateTime.now(),0L, null, null);
        Post post2 = new Post(null, UUID.randomUUID().toString(), null,
                UUID.randomUUID().toString(), LocalDateTime.now(),0L, null, null);
        Post post3 = new Post(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null, LocalDateTime.now(),0L, null, null);

        return Stream.of(post1, post2, post3);
    }
}
