package ru.practicum.myblog5.service;

import ru.practicum.myblog5.dto.Paging;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.dto.PostShortDto;
import ru.practicum.myblog5.dto.RequestPost;

import java.util.List;

public interface PostService {

    Long create(RequestPost requestPost);

    void update(Long postId, RequestPost post);

    void remove(Long postId);

    List<PostShortDto> getAll(Paging paging);

    void addLike(Long postId, boolean like);

    PostDto getById(Long postId);

    Paging getPaging(String search, int page, int size);
}
