package ru.practicum.myblog5.service;

import ru.practicum.myblog5.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentSearchService {

    List<Comment> getAllByPostId(Long postId);

    Map<Long, List<Comment>> getAllByPostIds(List<Long> postIds);
}
