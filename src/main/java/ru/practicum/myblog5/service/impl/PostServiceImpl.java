package ru.practicum.myblog5.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.myblog5.dto.Paging;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.dto.PostShortDto;
import ru.practicum.myblog5.dto.RequestPost;
import ru.practicum.myblog5.mapper.PostMapper;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.model.Post;
import ru.practicum.myblog5.model.Tag;
import ru.practicum.myblog5.repository.PostRepository;
import ru.practicum.myblog5.service.CommentSearchService;
import ru.practicum.myblog5.service.ImageService;
import ru.practicum.myblog5.service.PostService;
import ru.practicum.myblog5.service.TagService;
import ru.practicum.myblog5.service.validator.PostValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentSearchService commentSearchService;
    private final TagService tagService;
    private final ImageService imageService;
    private final PostValidator postValidator;

    public PostServiceImpl(PostRepository postRepository,
                           CommentSearchService commentSearchService,
                           TagService tagService,
                           ImageService imageService,
                           PostValidator postValidator) {
        this.postRepository = postRepository;
        this.commentSearchService = commentSearchService;
        this.tagService = tagService;
        this.imageService = imageService;
        this.postValidator = postValidator;
    }

    @Override
    @Transactional
    public Long create(RequestPost requestPost) {
        postValidator.requestValidate(requestPost);

        final String imageName = imageService.save(requestPost.getImage());
        Post post = PostMapper.requestToPost(requestPost, imageName);

        Long postId = postRepository.save(post).getId();
        tagService.saveAll(postId, post.getTags());

        return postId;
    }

    @Override
    @Transactional
    public void update(Long postId, RequestPost post) {
        postValidator.requestValidate(post);

        Post oldPost = getPost(postId);
        imageService.delete(oldPost.getImage());
        String imageName = imageService.save(post.getImage());
        Post updatedPost = PostMapper.requestToPost(post, imageName);
        updatedPost.setId(oldPost.getId());
        postRepository.save(updatedPost);

        tagService.deletePostTagsByPostId(oldPost.getId());
        tagService.saveAll(updatedPost.getId(), updatedPost.getTags());
    }

    @Override
    public void remove(Long postId) {
        Post post = getPost(postId);
        imageService.delete(post.getImage());
        postRepository.delete(post);
    }

    @Override
    public List<PostShortDto> getAll(Paging paging) {
        List<Post> posts = postRepository.findByParam(paging.getSearch(), paging.getOffset(), paging.getSize());
        assignComments(posts);
        assignTags(posts);

        return posts.stream()
                .map(PostMapper::toShortDto)
                .toList();
    }

    @Override
    public void addLike(Long postId, boolean like) {
        Post post = getPost(postId);
        int changer = like ? 1 : -1;
        postRepository.changeLikes(post.getId(), changer);
    }

    @Override
    public PostDto getById(Long postId) {
        Post post = getPost(postId);

        List<Comment> comments = commentSearchService.getAllByPostId(postId);
        post.setComments(comments);

        List<Tag> tags = tagService.getAllByPostId(postId);
        post.setTags(tags);

        return PostMapper.toPostDto(post);
    }

    @Override
    public Paging getPaging(String search, int page, int size) {
        Long count = postRepository.countByTag(search);

        if (count < 1) count = 1L;

        int maxPages = (int) (count % size == 0
                ? count / size
                : count / size + 1);

        int[] pageSizes = {5, 10, 20, 50};

        return Paging.builder()
                .search(search)
                .size(size)
                .pageSizes(pageSizes)
                .pageCount(maxPages)
                .page(Math.min(maxPages, page))
                .build();
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found")
        );
    }


    private void assignComments(List<Post> posts) {
        if (!posts.isEmpty()) {
            List<Long> postIds = posts.stream().map(Post::getId).toList();
            Map<Long, List<Comment>> comments = commentSearchService.getAllByPostIds(postIds);

            posts.forEach(post -> {
                List<Comment> commentList = comments.getOrDefault(post.getId(), new ArrayList<>());
                post.setComments(commentList);
            });
        }
    }

    private void assignTags(List<Post> posts) {
        if (!posts.isEmpty()) {
            List<Long> postIds = posts.stream().map(Post::getId).toList();
            Map<Long, List<Tag>> tags = tagService.getAllByPostIds(postIds);

            posts.forEach(post -> {
                List<Tag> tagList = tags.getOrDefault(post.getId(), new ArrayList<>());
                post.setTags(tagList);
            });
        }
    }
}
