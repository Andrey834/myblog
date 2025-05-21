package ru.practicum.myblog5.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.repository.CommentRepository;
import ru.practicum.myblog5.service.CommentService;
import ru.practicum.myblog5.service.PostService;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Override
    public void create(Long postId, String message) {
        PostDto postDto = postService.getById(postId);

        Comment newComment = Comment.builder()
                .postId(postDto.id())
                .text(message)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(newComment);
    }

    @Override
    public void update(Long postId, Long commentId, String message) {
        PostDto postDto = postService.getById(postId);

        if (postDto != null) {
            Comment comment = findById(commentId);
            comment.setText(message);
            commentRepository.save(comment);
        }
    }

    @Override
    public void delete(Long postId, Long commentId) {
        PostDto postDto = postService.getById(postId);
        Comment comment = findById(commentId);

        if (comment.getPostId().equals(postDto.id())) {
            commentRepository.delete(comment);
        }
    }

    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found"));
    }
}
