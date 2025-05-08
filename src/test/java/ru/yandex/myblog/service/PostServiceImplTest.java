package ru.yandex.myblog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.service.impl.PostServiceImpl;
import ru.yandex.myblog.configuration.ServiceConfig;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = { ServiceConfig.class, PostServiceImpl.class })
@ActiveProfiles(profiles = "unit-test")
class PostServiceImplTest {
    @Autowired
    private PostService postService;
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private ImageService imageService;
    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private TagRepository tagRepository;
    @MockitoBean
    private PostTagRepository postTagRepository;


    @BeforeEach
    void setUp() {
        reset(postRepository, tagRepository, postTagRepository);
    }

    @ParameterizedTest
    @MethodSource("provideValidFieldsInRequestPost")
    @DisplayName("Create Post when valid fields")
    void testCreate_whenValidFields(RequestPost requestPost) {
        Long expectedPostId = 1L;

        when(imageService.save(any())).thenReturn("test.jpg");
        when(postRepository.save(any())).thenReturn(expectedPostId);

        Long actualPostId = postService.create(requestPost);

        verify(imageService, times(1)).save(any());
        verify(postRepository, times(1)).save(any());
        assertEquals(expectedPostId, actualPostId);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFieldsInRequestPost")
    @DisplayName("Create Post when invalid fields")
    void testCreate_whenInvalidFields_thenException(RequestPost requestPost) {
        assertThrows(IllegalArgumentException.class, () -> postService.create(requestPost));

        verify(imageService, times(0)).save(any());
        verify(postRepository, times(0)).save(any());
        verify(tagRepository, times(0)).saveAll(any());
        verify(postTagRepository, times(0)).saveAll(any());
    }


    @Test
    @DisplayName("Update Post when valid fields")
    void testUpdate_whenValidFields_success() {
        RequestPost updatePost = new RequestPost("Test Post", "asdfasdfasdf", "sd gf rd fdg", getTestFile());

        doNothing().when(imageService).delete(anyString());
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        postService.update(1L, updatePost);

        verify(imageService, times(1)).save(any());
        verify(postRepository, times(1)).update(any());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFieldsInRequestPost")
    @DisplayName("Create Post when invalid fields")
    void testUpdate_whenInvalidFields_exception(RequestPost requestPost) {
        assertThrows(IllegalArgumentException.class, () -> postService.create(requestPost));

        verify(imageService, times(0)).delete(any());
        verify(imageService, times(0)).save(any());
        verify(postRepository, times(0)).update(any());
        verify(tagRepository, times(0)).saveAll(any());
        verify(postTagRepository, times(0)).saveAll(any());
    }

    @Test
    @DisplayName("Remove Post when ID is VALID")
    void testRemove_whenIdIsValid() {
        Long validPostId = 1L;
        Post post = new Post(validPostId,
                "Test",
                "testtesttest",
                null,
                LocalDateTime.now(),
                0L,
                null,
                null
        );

        when(postRepository.findById(validPostId)).thenReturn(Optional.of(post));
        doNothing().when(imageService).delete(any());

        postService.remove(validPostId);
        verify(imageService, times(1)).delete(any());
        verify(postRepository, times(1)).delete(validPostId);
    }

    @Test
    @DisplayName("Remove Post when ID is INVALID")
    void testRemove_whenIdIsInvalid() {
        Long invalidPostId = 99999L;

        when(postRepository.findById(invalidPostId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.remove(invalidPostId));

        verify(imageService, times(0)).delete(any());
        verify(postRepository, times(1)).findById(invalidPostId);
        verify(postRepository, times(0)).delete(invalidPostId);
    }

    @Test
    @DisplayName("Get Post when ID is VALID")
    void testGetById_whenIdIsValid() {
        Post exceptedPost = new Post(
                1L,
                "Test",
                "testtesttest",
                null,
                LocalDateTime.now(),
                0L,
                null,
                null
        );

        when(postRepository.findById(exceptedPost.getId())).thenReturn(Optional.of(exceptedPost));
        doReturn(List.of()).when(tagService).getAllByPostId(exceptedPost.getId());
        doReturn(List.of()).when(commentService).getAllByPostId(exceptedPost.getId());

        PostDto actualPost = postService.getById(exceptedPost.getId());
        verify(postRepository, times(1)).findById(exceptedPost.getId());
        verify(tagService, times(1)).getAllByPostId(exceptedPost.getId());
        verify(commentService, times(1)).getAllByPostId(exceptedPost.getId());

        assertEquals(exceptedPost.getId(), actualPost.id(), "Wrong post id");
    }

    @Test
    @DisplayName("Get Post when ID is INVALID")
    void testGetById_whenIdIsInvalid_thenException() {
        Long id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.getById(id));
        verify(postRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllByParam_whenInvokeWith_thenReturnList() {
        Paging paging = new Paging(1, 2, "cool", 5, new int[]{2, 5, 10, 20});
        Map<Long, Post> postMap = new HashMap<>();
        Post post1 = new Post(1L, "Test", "testtesttest", null, LocalDateTime.now(), 0L, List.of(), List.of());
        Post post2 = new Post(2L, "Test2", "testtesttest2", null, LocalDateTime.now(), 0L, List.of(), List.of());
        postMap.put(post1.getId(), post1);
        postMap.put(post2.getId(), post2);

        when(postRepository.findAll(paging)).thenReturn(postMap);
        when(tagService.getAllByPostIds(postMap.keySet())).thenReturn(new HashMap<>());


        List<PostShortDto> list = postService.getAllByParam(paging);

        verify(postRepository, times(1)).findAll(paging);
        verify(tagRepository, times(0)).findAllByPostIds(anyList());

        assertEquals(postMap.size(), list.size(), "Wrong collection size");
    }

    @Test
    void testAddLike_whenInvokeWithValidId_success() {
        Post post = new Post();
        post.setId(1L);

        doNothing().when(postRepository).changeLikes(post.getId(), 1);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.addLike(1L, true);
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).changeLikes(post.getId(), 1);
    }

    @Test
    void testAddLike_whenInvokeWithInvalidId_exception() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.addLike(1L, true));

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(0)).changeLikes(1L, 1);
    }

    @Test
    void assignMaxPage() {
        Paging paging = new Paging(1, 5, "cool", 5, new int[]{2, 5, 10, 20});

        when(postRepository.quantityByTag(any())).thenReturn(10L);

        Paging result = postService.assignMaxPage(paging);
        int expectedPages = 2;

        verify(postRepository, times(1)).quantityByTag(any());
        assertEquals(expectedPages, result.getPageCount(), "Wrong number of pages");
    }

    private static MultipartFile getTestFile() {
        return new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );
    }

    private static Stream<RequestPost> provideValidFieldsInRequestPost() {
        String lowRangeTitle = "a".repeat(3);
        String highRangeTitle = "a".repeat(250);

        String lowRangeText = "t".repeat(10);
        String highRangeText = "t".repeat(5000);

        return Stream.of(
                new RequestPost(lowRangeTitle, lowRangeText, "fgr bvg", getTestFile()),
                new RequestPost(highRangeTitle, highRangeText, "sd gf rd fdg", getTestFile()),
                new RequestPost(lowRangeTitle, highRangeText, "sd", getTestFile()),
                new RequestPost(highRangeTitle, lowRangeText, "sd gf", getTestFile())
        );
    }


    private static Stream<RequestPost> provideInvalidFieldsInRequestPost() {
        String longTitle = "a".repeat(251);
        String longText = "t".repeat(5001);

        return Stream.of(
                new RequestPost(),
                new RequestPost("", "test test test", "sd gf rd", getTestFile()),
                new RequestPost("TT", "test test test", "sd gf rd", getTestFile()),
                new RequestPost(longTitle, "test test test", "sd gf rd", getTestFile()),
                new RequestPost(null, "test test test", "sd gf rd", getTestFile()),
                new RequestPost("Test", "", "sd gf rd", getTestFile()),
                new RequestPost("Test", "testtest", "sd gf rd", getTestFile()),
                new RequestPost("Test", longText, "sd gf rd", getTestFile()),
                new RequestPost("Test", null, "sd gf rd", getTestFile()),
                new RequestPost("Test", "test test test", "", getTestFile()),
                new RequestPost("Test", "test test test", null, getTestFile()),
                new RequestPost("Test", "test test test", "sd gf rd", null)
        );
    }
}
