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
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.repository.CommentRepository;
import ru.yandex.myblog.service.impl.CommentServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {ServiceConfig.class, CommentServiceImpl.class})
@ActiveProfiles(profiles = "unit-test")
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;
    @MockitoBean
    private CommentRepository commentRepository;
    private final Long postId = 1L;
    private final Long commentId = 1L;

    @Test
    void addComment() {
        String commentText = "lalala";

        commentService.addComment(postId, commentText);

        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void deleteComment() {

        commentService.deleteComment(postId, commentId);

        verify(commentRepository, times(1)).delete(postId, commentId);
    }

    @Test
    void updateComment_success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(new Comment()));

        String newCommentText = "nananana";

        commentService.updateComment(postId, commentId, newCommentText);

        verify(commentRepository, times(1)).update(any());
    }

    @Test
    void updateComment_exception() {
        String newCommentText = "nananana";

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(postId, commentId, newCommentText));

        verify(commentRepository, times(0)).update(any());
    }

    @Test
    void getAllByPostId() {
        commentService.getAllByPostId(postId);
        verify(commentRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    void getAllByPostIds() {
        commentService.getAllByPostIds(Set.of(postId));
        verify(commentRepository, times(1)).findAllByPostIds(Set.of(postId));
    }
}
