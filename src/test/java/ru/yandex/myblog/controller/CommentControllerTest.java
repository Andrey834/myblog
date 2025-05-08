package ru.yandex.myblog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.configuration.WebConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.model.Post;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
class CommentControllerTest extends PostgresTestContainer {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private MockMvc mockMvc;
    private Post post1;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("""
                INSERT INTO post (title, text, image, created, likes) \
                VALUES ('test1', 'testtesttest1', 'image1.jpg', '2025-05-05 12:49:01.569672', 0);
                """
        );

        post1 = jdbcTemplate.queryForObject("select * from post where title like 'test1'",
                new BeanPropertyRowMapper<>(Post.class));

        assertNotNull(post1);

        String textComment = UUID.randomUUID().toString();

        jdbcTemplate.update(
                "insert into comment(post_id, text, created) values (?, ?, ?);",
                post1.getId(),
                textComment,
                LocalDateTime.now()
        );

        comment1 = jdbcTemplate.queryForObject("select * from comment where text like ?",
                new BeanPropertyRowMapper<>(Comment.class), textComment);

        assertNotNull(comment1);
    }

    @Test
    void addComment()  throws Exception {
        String message = "hellohello";

        mockMvc.perform(post("/posts/" + post1.getId() + "/comments")
                        .param("message", message))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        String savedComment = jdbcTemplate.queryForObject(
                "select text from comment where text like ?", String.class, message);

        assertEquals(message, savedComment);
    }

    @Test
    void updateComment() throws Exception  {
        String newMessage = "newMessage";

        mockMvc.perform(post("/posts/" + post1.getId() + "/comments/" + comment1.getId())
                        .param("message", newMessage))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        comment1 = jdbcTemplate.queryForObject("select * from comment where id = ?",
                new BeanPropertyRowMapper<>(Comment.class), comment1.getId());

        assertNotNull(comment1);

        assertEquals(newMessage, comment1.getText());
    }

    @Test
    void deleteComment() throws Exception  {
        mockMvc.perform(post(
                "/posts/" + post1.getId() + "/comments/" + comment1.getId() + "/delete")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        Long sizeComment = jdbcTemplate.queryForObject("select count(*) from comment", Long.class);
        assertNotNull(sizeComment);
        assertEquals(0, sizeComment);
    }
}
