package ru.practicum.myblog5.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.testcontainers.utility.TestcontainersConfiguration;
import ru.practicum.myblog5.config.PostgresTestContainer;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.dto.PostShortDto;
import ru.practicum.myblog5.enums.ViewName;
import ru.practicum.myblog5.model.Post;
import ru.practicum.myblog5.repository.PostRepository;
import ru.practicum.myblog5.service.ImageService;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest extends PostgresTestContainer {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @MockitoBean
    private ImageService imageService;
    private Post post1;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        post1 = Post.builder()
                .title("title")
                .image("image")
                .likes(0L)
                .text("testtesttes")
                .created(LocalDateTime.now())
                .build();

        file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        postRepository.deleteAll();
        postRepository.save(post1);


    }

    @Test
    void viewAllPosts() throws Exception {
        MvcResult result = mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andReturn();

        List<PostShortDto> posts = (List<PostShortDto>) result.getModelAndView().getModel().get("posts");
        assertEquals(1, posts.size());

        PostShortDto postShortDto = posts.getFirst();
        assertEquals(post1.getTitle(), postShortDto.title());
    }

    @Test
    void viewPost() throws Exception {
        MvcResult result = mockMvc.perform(get("/posts/{id}", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name(ViewName.POST.getValue()))
                .andExpect(model().attributeExists("post")).andReturn();

        ModelAndView modelAndView = result.getModelAndView();

        assertNotNull(modelAndView);
        PostDto postDto = (PostDto) modelAndView.getModel().get("post");
        assertEquals(post1.getTitle(), postDto.title());
        assertEquals(post1.getImage(), postDto.image());
    }

    @Test
    void viewPublish() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name(ViewName.PUBLISH.getValue()))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void processAddPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        String title = UUID.randomUUID().toString();

        List<Post> posts = getPosts();

        mockMvc.perform(multipart("/posts/add")
                        .file("image", file.getBytes())
                        .param("title", title)
                        .param("text", "test8test8test8")
                        .param("tags", "cool bad")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + getPosts().stream()
                        .filter(post -> !posts.contains(post))
                        .findFirst()
                        .map(Post::getId)
                        .orElse(0L))
                );
    }

    @Test
    void viewEditPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        when(imageService.get(any()))
                .thenReturn(new UrlResource(Path.of("src/test/resources/templates/img/promo-test.jpg").toUri()));

        mockMvc.perform(get("/posts/" + post1.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("publish"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void processEditPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        when(imageService.get(any())).thenReturn(any(UrlResource.class));

        assertNotNull(post1);

        String newTitle = "newTitle";
        String newText = "newtext_foredit";

        mockMvc.perform(multipart("/posts/" + post1.getId() + "/edit")
                        .file("image", file.getBytes())
                        .param("title", newTitle)
                        .param("text", newText)
                        .param("tags", "happy new edit")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        Optional<Post> optionalPost = postRepository.findById(post1.getId());
        assertTrue(optionalPost.isPresent());
        Post post = optionalPost.get();

        assertNotNull(post);
        assertEquals(newTitle, post.getTitle());
        assertEquals(newText, post.getText());
    }

    @Test
    void deletePost() throws Exception {
        Post post = postRepository.save(post1);
        List<Post> posts = getPosts();
        int actual = posts.size();
        assertEquals(1, actual);

        mockMvc.perform(post("/posts/" + post.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        posts = getPosts();
        int expectedSize = actual - 1;
        assertEquals(expectedSize, posts.size());
    }

    @Test
    void processAddLike() throws Exception {
        Post post = postRepository.save(post1);
        Long expectedLikes = 0L;
        assertEquals(expectedLikes, post.getLikes());

        mockMvc.perform(post("/posts/" + post.getId() + "/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        Optional<Post> optionalPost = postRepository.findById(post.getId());
        assertTrue(optionalPost.isPresent());
        post = optionalPost.get();

        expectedLikes = 1L;
        assertEquals(expectedLikes, post.getLikes());
    }

    private List<Post> getPosts() {
        return postRepository.findByParam("", 0, 100);
    }
}
