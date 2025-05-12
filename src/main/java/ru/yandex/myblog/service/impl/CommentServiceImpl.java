package ru.yandex.myblog.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.repository.CommentRepository;
import ru.yandex.myblog.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void addComment(Long postId, String comment) {
        Comment newComment = Comment.builder()
                .postId(postId)
                .text(comment)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(newComment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        commentRepository.delete(postId, commentId);
    }

    @Override
    public void updateComment(Long postId, Long commentId, String message) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found"));
        comment.setText(message);
        commentRepository.update(comment);
    }

    @Override
    public List<Comment> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public List<Comment> getAllByPostIds(Set<Long> postIds) {
        return commentRepository.findAllByPostIds(postIds);
    }
}
