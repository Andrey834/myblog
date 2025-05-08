package ru.yandex.myblog.service;

import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;

import java.util.List;

public interface PostService {

    Long create(RequestPost requestPost);

    void update(Long id, RequestPost post);

    PostDto getById(Long id);

    List<PostShortDto> getAllByParam(Paging paging);

    Paging assignMaxPage(Paging paging);

    void remove(Long postId);

    void addLike(Long postId, boolean isLike);
}
