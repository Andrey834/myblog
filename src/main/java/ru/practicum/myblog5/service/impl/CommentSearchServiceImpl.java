package ru.practicum.myblog5.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.repository.CommentRepository;
import ru.practicum.myblog5.service.CommentSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentSearchServiceImpl implements CommentSearchService {
    private final CommentRepository commentRepository;

    public CommentSearchServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Map<Long, List<Comment>> getAllByPostIds(List<Long> postIds) {
        List<Comment> comments = commentRepository.findAllByPostIds(postIds);
        Map<Long, List<Comment>> map = new HashMap<>();

        comments.forEach(comment -> {
            map.putIfAbsent(comment.getPostId(), new ArrayList<>());
            map.get(comment.getPostId()).add(comment);
        });

        return map;
    }
}
