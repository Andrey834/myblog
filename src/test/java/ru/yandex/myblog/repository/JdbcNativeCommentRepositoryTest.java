package ru.yandex.myblog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.repository.impl.JdbcNativeCommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeCommentRepository.class})
class JdbcNativeCommentRepositoryTest extends PostgresTestContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcNativeCommentRepository commentRepository;
    private Long postId1;
    private Long postId2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("DELETE FROM post");

        String query = """
                INSERT INTO post (title, text, image) \
                VALUES \
                ('test1', 'long_test1', 'image1.jpg'), \
                ('test2', 'long_test2', 'image2.jpg'), \
                ('test3', 'long_test3', 'image3.jpg');
                """;

        jdbcTemplate.execute(query);
        postId1 = jdbcTemplate.queryForObject("SELECT id FROM post WHERE image = 'image1.jpg'", Long.class);
        postId2 = jdbcTemplate.queryForObject("SELECT id FROM post WHERE image = 'image2.jpg'", Long.class);

    }

    @Test
    void testSave_shouldAddCommentToDatabase() {
        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        int expectedSize = 0;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 0");

        Comment newComment = getComment(postId1);
        commentRepository.save(newComment);

        actualComments = commentRepository.findAllByPostId(postId1);
        expectedSize = 1;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 1");

        Comment updatedComment = actualComments.getFirst();

        assertEquals(newComment.getPostId(), updatedComment.getPostId());
        assertEquals(newComment.getText(), updatedComment.getText());
        assertEquals(newComment.getCreated(), updatedComment.getCreated());
    }

    @Test
    void testSave_whenAddInvalidPostId_shouldThrowException() {
        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        int expectedSize = 0;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 0");

        Long invalidPostId = 99999L;
        Comment newComment = getComment(invalidPostId);

        assertThrows(DataIntegrityViolationException.class,
                () -> commentRepository.save(newComment));

        actualComments = commentRepository.findAllByPostId(postId1);
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 0");
    }

    @Test
    void testUpdate_shouldUpdateComment() {
        String oldMessage = "old message";
        Comment newComment = getComment(postId1);
        newComment.setText(oldMessage);
        commentRepository.save(newComment);

        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        Comment savedComment = actualComments.getFirst();

        assertEquals(oldMessage, savedComment.getText());

        String newMessage = "new message";
        savedComment.setText(newMessage);
        commentRepository.update(savedComment);

        actualComments = commentRepository.findAllByPostId(postId1);
        savedComment = actualComments.getFirst();

        assertEquals(newMessage, savedComment.getText());
    }

    @Test
    void testFindById_whenPostIdValid_shouldReturnComment() {
        Comment newComment = getComment(postId1);
        commentRepository.save(newComment);

        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        Comment savedComment = actualComments.getFirst();

        Optional<Comment> actualComment = commentRepository.findById(savedComment.getId());
        assertTrue(actualComment.isPresent());

        assertEquals(newComment.getPostId(), actualComment.get().getPostId());
        assertEquals(newComment.getText(), actualComment.get().getText());
        assertEquals(newComment.getCreated(), actualComment.get().getCreated());
    }

    @Test
    void testFindById_whenPostIdInvalid_shouldReturnEmptyOptional() {
       Long invalidPostId = 99999L;
        Optional<Comment> actualComment = commentRepository.findById(invalidPostId);
        assertFalse(actualComment.isPresent());
    }

    @Test
    void delete() {
        Comment newComment = getComment(postId1);
        commentRepository.save(newComment);
        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        Long commentId = actualComments.getFirst().getId();
        int expectedSize = 1;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 1");

        commentRepository.delete(postId1, commentId);
        actualComments = commentRepository.findAllByPostId(postId1);
        expectedSize = 0;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 0");
    }

    @Test
    void findAllByPostId() {
        Comment newComment1 = getComment(postId1);
        Comment newComment2 = getComment(postId1);
        Comment newComment3 = getComment(postId1);
        commentRepository.save(newComment1);
        commentRepository.save(newComment2);
        commentRepository.save(newComment3);

        List<Comment> actualComments = commentRepository.findAllByPostId(postId1);
        int expectedSize = 3;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 3");

        actualComments = commentRepository.findAllByPostId(postId2);
        expectedSize = 0;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 0");
    }

    @Test
    void findAllByPostIds() {
        Comment newComment1 = getComment(postId1);
        Comment newComment2 = getComment(postId1);
        Comment newComment3 = getComment(postId2);
        Comment newComment4 = getComment(postId2);
        commentRepository.save(newComment1);
        commentRepository.save(newComment2);
        commentRepository.save(newComment3);
        commentRepository.save(newComment4);

        List<Comment> actualComments = commentRepository.findAllByPostIds(Set.of(postId1, postId2));
        int expectedSize = 4;
        assertEquals(expectedSize, actualComments.size(), "Size of comment must be 4");
    }

    private Comment getComment(Long postId) {
        return Comment.builder()
                .postId(postId)
                .text(UUID.randomUUID().toString())
                .created(LocalDateTime.now())
                .build();
    }
}
