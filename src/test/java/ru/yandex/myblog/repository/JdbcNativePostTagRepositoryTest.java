package ru.yandex.myblog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.model.PostTag;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.impl.JdbcNativePostTagRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostTagRepository.class})
class JdbcNativePostTagRepositoryTest extends PostgresTestContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostTagRepository postTagRepository;
    private Long postId1;
    private Long postId2;
    List<Long> tagIds;
    List<Tag> tags;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("DELETE FROM tag");
        jdbcTemplate.execute("DELETE FROM post_tag");

        String query = """
                INSERT INTO post (title, text, image) \
                VALUES \
                ('test1', 'long_test1', 'image1.jpg'), \
                ('test2', 'long_test2', 'image2.jpg'), \
                ('test3', 'long_test3', 'image3.jpg');
                """;

        jdbcTemplate.execute(query);
        postId1 = jdbcTemplate.queryForObject("SELECT id FROM post WHERE image = 'image1.jpg'", Long.class);
        postId2 = jdbcTemplate.queryForObject("SELECT id FROM post WHERE image = 'image2.jpg'", Long.class);

        tags = getTagsListWithTitles("test1", "test2", "test3");
        tagIds = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            jdbcTemplate.update("insert into tag (title) values (?)", tags.get(i).getTitle());
            Long id = jdbcTemplate.queryForObject("select id from tag where title = ?", Long.class,
                    tags.get(i).getTitle());
            tagIds.add(id);
        }
    }

    @Test
    void saveAll() {
        Long size = jdbcTemplate.queryForObject("select count(*) from post_tag", Long.class);
        assertEquals(0, size);
        List<PostTag> postTags = getPostTagsList(postId1, tagIds);

        postTagRepository.saveAll(postTags);

        size = jdbcTemplate.queryForObject("select count(*) from post_tag", Long.class);
        assertEquals(tags.size(), size);

    }

    @Test
    void findAllByPostId() {
        List<PostTag> postTags = getPostTagsList(postId1, tagIds);
        postTagRepository.saveAll(postTags);

        int expectedSize = tags.size();
        List<PostTag> foundPostTags = postTagRepository.findAllByPostId(postId1);
        assertEquals(expectedSize, foundPostTags.size());

        expectedSize = 0;
        foundPostTags = postTagRepository.findAllByPostId(postId2);
        assertEquals(expectedSize, foundPostTags.size());

    }

    @Test
    void removeAllByPostId() {
        List<PostTag> postTags = getPostTagsList(postId1, tagIds);
        postTagRepository.saveAll(postTags);
        List<PostTag> foundPostTags = postTagRepository.findAllByPostId(postId1);
        int expectedSize = tags.size();
        assertEquals(expectedSize, foundPostTags.size());

        postTagRepository.removeAllByPostId(postId1);
        foundPostTags = postTagRepository.findAllByPostId(postId1);
        expectedSize = 0;
        assertEquals(expectedSize, foundPostTags.size());
    }

    private List<Tag> getTagsListWithTitles(String... titles) {
        List<Tag> tags = new ArrayList<>();
        for (var i = 0; i < titles.length; i++) {
            Tag tag = Tag.builder().title(titles[i]).build();
            tags.add(tag);
        }

        return tags;
    }

    private List<PostTag> getPostTagsList(Long postId, List<Long> tagIds) {
        List<PostTag> postTags = new ArrayList<>();
        tagIds.forEach(tagId -> {
            postTags.add(new PostTag(postId, tagId));
        });
        return postTags;
    }
}
