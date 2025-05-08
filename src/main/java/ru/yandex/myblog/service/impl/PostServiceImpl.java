package ru.yandex.myblog.service.impl;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;
import ru.yandex.myblog.mapper.PostMapper;
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.service.CommentService;
import ru.yandex.myblog.service.ImageService;
import ru.yandex.myblog.service.PostService;
import ru.yandex.myblog.service.TagService;
import ru.yandex.myblog.service.validator.PostValidator;

import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ImageService imageService;
    private final TagService tagService;
    private final CommentService commentService;
    private final PostValidator postValidator;

    public PostServiceImpl(PostRepository postRepository,
                           ImageService imageService,
                           TagService tagService,
                           CommentService commentService,
                           PostValidator postValidator) {
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.postValidator = postValidator;
    }

    @Override
    @Transactional
    public Long create(RequestPost requestPost) {
        postValidator.requestValidate(requestPost);

        final String imageName = imageService.save(requestPost.getImage());

        Post newPost = PostMapper.requestToPost(requestPost, imageName);
        Long postId = postRepository.save(newPost);

        tagService.saveAll(postId, newPost.getTags());
        return postId;
    }

    @Override
    @Transactional
    public void update(Long id, RequestPost requestPost) {
        postValidator.requestValidate(requestPost);

        Post oldPost = getPostById(id);

        imageService.delete(oldPost.getImage());
        String imageName = imageService.save(requestPost.getImage());

        Post updatePost = PostMapper.requestToPost(requestPost, imageName);
        updatePost.setId(oldPost.getId());
        postRepository.update(updatePost);

        tagService.deletePostTagsByPostId(id);
        tagService.saveAll(id, updatePost.getTags());
    }

    @Override
    @Transactional
    public void remove(Long postId) {
        Post post = getPostById(postId);
        imageService.delete(post.getImage());
        postRepository.delete(post.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getById(Long id) {
        Post post = getPostById(id);

        List<Tag> tags = tagService.getAllByPostId(post.getId());
        post.setTags(tags);

        List<Comment> comments = commentService.getAllByPostId(post.getId());
        post.setComments(comments);

        return PostMapper.toPostDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostShortDto> getAllByParam(Paging paging) {
        Map<Long, Post> postMap = postRepository.findAll(paging);

        if (!postMap.isEmpty()) {
            Map<Long, List<Tag>> tags = tagService.getAllByPostIds(postMap.keySet());
            tags.keySet().forEach(postId ->
                    postMap.get(postId).setTags(tags.get(postId))
            );

            List<Comment> comments = commentService.getAllByPostIds(postMap.keySet());
            comments.forEach(comment ->
                    postMap.get(comment.getPostId()).getComments()
                            .add(comment)
            );
        }

        return postMap.values().stream()
                .map(PostMapper::toPostShortDto)
                .toList();
    }

    @Override
    @Synchronized
    public void addLike(Long postId, boolean isLike) {
        Post post = getPostById(postId);
        int changer = isLike ? 1 : -1;
        postRepository.changeLikes(post.getId(), changer);
    }

    @Override
    @Transactional(readOnly = true)
    public Paging assignMaxPage(Paging paging) {
        Long quantity = postRepository.quantityByTag(paging.getSearch());
        int maxPages = (int) (quantity % paging.getSize() == 0
                ? quantity / paging.getSize()
                : quantity / paging.getSize() + 1);

        if (maxPages < paging.getPage()) {
            paging.setPage(maxPages);
        }
        paging.setPageCount(maxPages);

        return paging;
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}
