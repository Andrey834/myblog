package ru.yandex.myblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.configuration.WebConfiguration;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.service.ImageService;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
class PostControllerTest extends PostgresTestContainer {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @MockitoBean
    private ImageService imageService;

    private MockMvc mockMvc;
    private MockMultipartFile file;

    private Post post1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("""
                INSERT INTO post (title, text, image, created, likes) \
                VALUES \
                ('test1', 'testtesttest1', 'image1.jpg', '2025-05-05 12:49:01.569672', 0), \
                ('test2', 'testtesttest2', 'image2.jpg', '2025-05-05 12:49:01.569672', 0), \
                ('test3', 'testtesttest3', 'image3.jpg', '2025-05-05 12:49:01.569672', 0), \
                ('test4', 'testtesttest4', 'image4.jpg', '2025-05-05 12:49:01.569672', 0), \
                ('test5', 'testtesttest5', 'image5.jpg', '2025-05-05 12:49:01.569672', 0);
                """
        );

        file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        post1 = jdbcTemplate.queryForObject("select * from post where title like 'test1'",
                new BeanPropertyRowMapper<>(Post.class));

    }

    @Test
    void viewPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void viewPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        when(imageService.get(any()))
                .thenReturn(new UrlResource(Path.of("src/test/resources/templates/img/promo-test.jpg").toUri()));

        assertNotNull(post1);

        mockMvc.perform(get("/posts/" + post1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void viewPublish() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("publish"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void processAddPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        when(imageService.get(any()))
                .thenReturn(new UrlResource(Path.of("src/test/resources/templates/img/promo-test.jpg").toUri()));
        String title = UUID.randomUUID().toString();

        mockMvc.perform(multipart("/posts/add")
                        .file("image", file.getBytes())
                        .param("title", title)
                        .param("text", "test8test8test8")
                        .param("tags", "cool bad")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + jdbcTemplate.queryForObject(
                                "select id from post where title like ?", Long.class, title)
                        )
                );
    }

    @Test
    void viewEditPost() throws Exception {
        when(imageService.save(any())).thenReturn(UUID.randomUUID().toString());
        when(imageService.get(any()))
                .thenReturn(new UrlResource(Path.of("src/test/resources/templates/img/promo-test.jpg").toUri()));

        assertNotNull(post1);

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

        Post post = jdbcTemplate.queryForObject("select * from post where id = ?",
                new BeanPropertyRowMapper<>(Post.class), post1.getId());

        assertNotNull(post);
        assertEquals(newTitle, post.getTitle());
        assertEquals(newText, post.getText());
    }

    @Test
    void deletePost() throws Exception {
        Long size = jdbcTemplate.queryForObject("select count(id) from post", Long.class);
        assertNotNull(size);

        mockMvc.perform(post("/posts/" + post1.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        Long actualSize = jdbcTemplate.queryForObject("select count(id) from post", Long.class);
        assertEquals(size - 1, actualSize);
    }

    @Test
    void processAddLike() throws Exception {
        int expectedLikes = 0;
        assertEquals(expectedLikes, post1.getLikes());

        mockMvc.perform(post("/posts/" + post1.getId() + "/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        Long likes = jdbcTemplate.queryForObject(
                "select likes from post where title like 'test1'", Long.class);
        expectedLikes = 1;
        assertEquals(expectedLikes, likes);
    }
}
