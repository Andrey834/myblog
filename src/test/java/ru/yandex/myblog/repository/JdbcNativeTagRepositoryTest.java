package ru.yandex.myblog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.myblog.configuration.DataSourceConfiguration;
import ru.yandex.myblog.containers.PostgresTestContainer;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.repository.impl.JdbcNativeTagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeTagRepository.class})
class JdbcNativeTagRepositoryTest extends PostgresTestContainer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagRepository tagRepository;
    private Long postId1;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("DELETE FROM tag");

        String query = """
                INSERT INTO post (title, text, image) \
                VALUES \
                ('test1', 'long_test1', 'image1.jpg'), \
                ('test2', 'long_test2', 'image2.jpg'), \
                ('test3', 'long_test3', 'image3.jpg');
                """;

        jdbcTemplate.execute(query);
        postId1 = jdbcTemplate.queryForObject("SELECT id FROM post WHERE image = 'image1.jpg'", Long.class);
    }

    @Test
    void testSaveAll_shouldSaveAllTags() {
        int expectedSize = 0;
        String query = "SELECT count(id) FROM tag";

        Long tagSize = jdbcTemplate.queryForObject(query, Long.class);
        assertEquals(expectedSize, tagSize);

        expectedSize = 20;
        List<Tag> tags = getTagsList((long) expectedSize);
        tagRepository.saveAll(tags);

        tagSize = jdbcTemplate.queryForObject(query, Long.class);
        assertEquals(expectedSize, tagSize);
    }

    @Test
    void testSaveAll_whenRepeatTitle_shouldSaveOneTag() {
        int expectedSize = 0;
        String query = "SELECT count(id) FROM tag";

        Long tagSize = jdbcTemplate.queryForObject(query, Long.class);
        assertEquals(expectedSize, tagSize);


        List<Tag> tags = getTagsListWithTitles("repeat", "repeat");
        tagRepository.saveAll(tags);
        expectedSize = 1;
        tagSize = jdbcTemplate.queryForObject(query, Long.class);
        assertEquals(expectedSize, tagSize);
    }

    @Test
    void findAllByTitle() {
        String[] strTags = new String[]{"tag1", "tag2", "tag3", "tag4", "tag5"};
        List<Tag> tags = getTagsListWithTitles(strTags);
        tagRepository.saveAll(tags);

        List<Tag> actualTags = tagRepository.findAllByTitle(List.of(strTags));
        assertEquals(strTags.length, actualTags.size());
    }

    @Test
    void findAllByPostIds() {
        String[] strTags = new String[]{"tag1", "tag2", "tag3", "tag4", "tag5"};
        List<Tag> tags = getTagsListWithTitles(strTags);
        tagRepository.saveAll(tags);

        List<Tag> actualTags = tagRepository.findAllByTitle(List.of(strTags));

        int expectedSize = 3;
        actualTags.stream().limit(expectedSize).forEach(tag -> {
            jdbcTemplate.update("INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)", postId1, tag.getId());
        });

        Map<Long, List<Tag>> actualTagsWithPostId = tagRepository.findAllByPostIds(List.of(postId1));
        assertEquals(expectedSize, actualTagsWithPostId.get(postId1).size());
    }

    @Test
    void delete() {
        String[] strTags = new String[]{"tag1"};
        List<Tag> tags = getTagsListWithTitles(strTags);
        tagRepository.saveAll(tags);

        List<Tag> actualTags = tagRepository.findAllByTitle(List.of(strTags));
        assertEquals(1, actualTags.size());

        tagRepository.delete(actualTags.getFirst().getId());

        actualTags = tagRepository.findAllByTitle(List.of(strTags));
        assertEquals(0, actualTags.size());
    }

    private List<Tag> getTagsList(Long size) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Tag tag = Tag.builder().title(UUID.randomUUID().toString()).build();
            tags.add(tag);
        }

        return tags;
    }

    private List<Tag> getTagsListWithTitles(String... titles) {
        List<Tag> tags = new ArrayList<>();
        for (var i = 0; i < titles.length; i++) {
            Tag tag = Tag.builder().title(titles[i]).build();
            tags.add(tag);
        }

        return tags;
    }
}
