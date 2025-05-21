package ru.practicum.myblog5.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.repository.CommentRepository;
import ru.practicum.myblog5.repository.PostRepository;
import ru.practicum.myblog5.service.impl.CommentServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommentServiceImpl.class)
class CommentServiceImplTest {
    @MockitoBean
    private CommentRepository commentRepository;
    @MockitoBean
    private PostService postService;
    @MockitoBean
    private PostRepository postRepository;
    @Autowired
    private CommentServiceImpl commentService;

    private Comment comment;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @BeforeEach
    void setUp() {
        comment = new Comment(null, 1L, "Hello", LocalDateTime.now());
    }

    @Test
    @DisplayName("Create new comment")
    void testCreate_success() {
        when(commentRepository.save(comment)).thenReturn(comment);
        when(postService.getById(any())).thenReturn(PostDto.builder().id(comment.getPostId()).build());

        commentService.create(comment.getPostId(), comment.getText());
        verify(commentRepository, times(1)).save(commentCaptor.capture());

        assertEquals(comment.getPostId(), commentCaptor.getValue().getPostId());
        assertEquals(comment.getText(), commentCaptor.getValue().getText());
        assertNotNull(commentCaptor.getValue().getCreated());
    }

    @Test
    void testUpdate_success() {
        String expectedText = "Bye";
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(postService.getById(any())).thenReturn(PostDto.builder().id(comment.getPostId()).build());


        commentService.update(comment.getPostId(), comment.getId(), expectedText);
        verify(commentRepository, times(1)).save(commentCaptor.capture());

        assertEquals(comment.getPostId(), commentCaptor.getValue().getPostId());
        assertEquals(expectedText, commentCaptor.getValue().getText());
    }

    @Test
    void testDelete_success() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(postService.getById(comment.getPostId()))
                .thenReturn(PostDto.builder().id(comment.getPostId()).build());
        doNothing().when(commentRepository).delete(comment);

        commentService.delete(comment.getPostId(), comment.getId());

        verify(commentRepository, times(1)).delete(comment);
        verify(postService, times(1)).getById(comment.getPostId());
    }
}
