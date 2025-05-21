package ru.practicum.myblog5.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.myblog5.dto.Paging;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.dto.PostShortDto;
import ru.practicum.myblog5.dto.RequestPost;
import ru.practicum.myblog5.model.Comment;
import ru.practicum.myblog5.model.Post;
import ru.practicum.myblog5.model.Tag;
import ru.practicum.myblog5.repository.PostRepository;
import ru.practicum.myblog5.service.impl.PostServiceImpl;
import ru.practicum.myblog5.service.validator.PostValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PostServiceImpl.class, PostValidator.class})
class PostServiceImplTest {
    @Autowired
    private PostService postService;
    @MockitoBean
    private ImageService imageService;
    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private CommentSearchService commentSearchService;


    private Post post1;
    private Post post2;
    private RequestPost requestPost;
    private Paging paging;

    @BeforeEach
    void setUp() {
        post1 = getPost(1L);
        post2 = getPost(2L);

        paging = Paging.builder()
                .search("")
                .page(1)
                .size(10)
                .pageSizes(new int[]{5, 10, 20, 50})
                .build();

        requestPost = RequestPost.builder()
                .title(post1.getTitle())
                .text(post1.getText())
                .image(new MockMultipartFile(
                        "test.jpg",
                        "test.jpg",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes()))
                .tags("as ds")
                .build();
    }

    @Test
    void create() {
        String imageName = UUID.randomUUID().toString();

        when(imageService.save(any())).thenReturn(imageName);
        when(postRepository.save(any())).thenReturn(post1);
        doNothing().when(tagService).saveAll(anyLong(), anyList());

        Long actualId = postService.create(requestPost);

        assertNotNull(actualId);
        assertEquals(post1.getId(), actualId);

        verify(imageService, times(1)).save(any());
        verify(postRepository, times(1)).save(any());
        verify(tagService, times(1)).saveAll(anyLong(), anyList());
    }

    @Test
    void update() {
        String imageName = UUID.randomUUID().toString();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post1));
        when(imageService.save(any())).thenReturn(imageName);
        doNothing().when(tagService).saveAll(anyLong(), anyList());
        doNothing().when(tagService).deletePostTagsByPostId(anyLong());

        postService.update(post1.getId(), requestPost);


        verify(imageService, times(1)).save(any());
        verify(postRepository, times(1)).save(any());
        verify(postRepository, times(1)).findById(post1.getId());
        verify(tagService, times(1)).saveAll(anyLong(), anyList());
    }

    @Test
    void remove() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post1));
        doNothing().when(postRepository).delete(post1);
        doNothing().when(imageService).delete(anyString());

        postService.remove(post1.getId());

        verify(imageService, times(1)).delete(anyString());
        verify(postRepository, times(1)).findById(post1.getId());
        verify(postRepository, times(1)).delete(post1);
    }

    @Test
    void getAll() {
        Map<Long, List<Comment>> comments = new HashMap<>();
        comments.put(post1.getId(), getComments(post1.getId(), 10));
        comments.put(post2.getId(), getComments(post2.getId(), 20));
        when(commentSearchService.getAllByPostIds(List.of(post1.getId(), post2.getId())))
                .thenReturn(comments);

        Map<Long, List<Tag>> tags = new HashMap<>();
        tags.put(post1.getId(), getTags(10));
        tags.put(post2.getId(), getTags(20));
        when(tagService.getAllByPostIds(List.of(post1.getId(), post2.getId())))
                .thenReturn(tags);

        when(postRepository.findByParam(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(post1, post2));

        List<PostShortDto> postShortDtos = postService.getAll(paging);

        int expectedSize = 2;
        assertEquals(expectedSize, postShortDtos.size());

        verify(postRepository, times(1))
                .findByParam(anyString(), anyInt(), anyInt());
        verify(commentSearchService, times(1))
                .getAllByPostIds(List.of(post1.getId(), post2.getId()));
        verify(tagService, times(1))
                .getAllByPostIds(List.of(post1.getId(), post2.getId()));

    }

    @Test
    void getAll_whenEmptyPosts_notShouldCallTagAndCommentSearchServices() {
        when(postRepository.findByParam(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        List<PostShortDto> postShortDtos = postService.getAll(paging);

        assertEquals(0, postShortDtos.size());

        verify(postRepository, times(1))
                .findByParam(anyString(), anyInt(), anyInt());
        verify(commentSearchService, times(0))
                .getAllByPostIds(anyList());
        verify(tagService, times(0))
                .getAllByPostIds(anyList());

    }

    @Test
    void addLike_whenTrue() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post1));

        postService.addLike(post1.getId(), true);

        verify(postRepository, times(1)).findById(post1.getId());
        verify(postRepository, times(1)).changeLikes(post1.getId(), 1);
    }

    @Test
    void addLike_whenFalse() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post1));

        postService.addLike(post1.getId(), false);

        verify(postRepository, times(1)).findById(post1.getId());
        verify(postRepository, times(1)).changeLikes(post1.getId(), -1);
    }

    @Test
    void testGetById_whenValidPostId_success() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post1));
        List<Comment> comments = getComments(post1.getId(), 10);
        when(commentSearchService.getAllByPostId(post1.getId())).thenReturn(comments);
        List<Tag> tags = getTags(10);
        when(tagService.getAllByPostId(post1.getId())).thenReturn(tags);

        PostDto postDto = postService.getById(post1.getId());

        assertNotNull(postDto);

        verify(postRepository, times(1)).findById(post1.getId());
        verify(commentSearchService, times(1)).getAllByPostId(post1.getId());
        verify(tagService, times(1)).getAllByPostId(post1.getId());
    }

    @Test
    void testGetById_whenInvalidPostId_thenException() {
        when(postRepository.findById(post1.getId())).thenReturn(Optional.empty());
        Long invalidId = -1L;
        assertThrows(IllegalArgumentException.class, () -> postService
                .getById(invalidId));
    }

    @Test
    void getPaging() {
        when(postRepository.countByTag(anyString())).thenReturn(10L);
        Paging actualPaging = postService.getPaging("", 1, 10);
        assertNotNull(actualPaging);
    }

    private Post getPost(Long postId) {
        return Post.builder()
                .id(postId)
                .title("test" + postId)
                .text("testTestTest" + postId)
                .image("test" + postId + ".jpg")
                .likes(0L)
                .created(LocalDateTime.now())
                .build();
    }

    private List<Comment> getComments(Long postId, int size) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Comment comment = Comment.builder()
                    .postId(postId)
                    .text(UUID.randomUUID().toString())
                    .created(LocalDateTime.now())
                    .build();

            comments.add(comment);
        }

        return comments;
    }

    private List<Tag> getTags(int size) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Tag tag = Tag.builder()
                    .id((long) i)
                    .title("title" + i)
                    .build();

            tags.add(tag);
        }

        return tags;
    }
}
