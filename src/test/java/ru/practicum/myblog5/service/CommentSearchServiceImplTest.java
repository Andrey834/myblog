package ru.practicum.myblog5.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.repository.CommentRepository;
import ru.practicum.myblog5.service.impl.CommentSearchServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommentSearchServiceImpl.class)
class CommentSearchServiceImplTest {
    @Autowired
    private CommentSearchService commentSearchService;
    @MockitoBean
    private CommentRepository commentRepository;
    private final Long postId = 1L;

    @Test
    void getAllByPostId() {
        List<Comment> comments = getComments(10);

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        List<Comment> result = commentSearchService.getAllByPostId(postId);
        assertEquals(comments, result);
        verify(commentRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    void getAllByPostIds() {
        List<Comment> comments = getComments(20);
        when(commentRepository.findAllByPostIds(anyList())).thenReturn(comments);

        Map<Long, List<Comment>> result = commentSearchService.getAllByPostIds(List.of(postId));

        assertEquals(comments, result.get(postId));
        verify(commentRepository, times(1))
                .findAllByPostIds(List.of(postId));
    }

    private List<Comment> getComments(int size) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Comment comment = Comment.builder()
                    .id((long) i)
                    .postId(postId)
                    .text("text" + i)
                    .created(LocalDateTime.now())
                    .build();

            comments.add(comment);
        }

        return comments;
    }
}
