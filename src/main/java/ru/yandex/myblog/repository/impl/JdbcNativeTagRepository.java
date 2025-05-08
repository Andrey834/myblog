package ru.yandex.myblog.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.yandex.myblog.model.Tag;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.repository.extractors.TagExtractor;

import java.util.List;
import java.util.Map;

@Repository
public class JdbcNativeTagRepository implements TagRepository {
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public JdbcNativeTagRepository(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public void saveAll(List<Tag> tags) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(tags.toArray());
        String query = """
                insert into tag (title) \
                values (:title) \
                ON CONFLICT DO NOTHING;
                """;

        parameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public List<Tag> findAllByTitle(List<String> tagTitles) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("tagTitles", tagTitles);
        String query = "SELECT id, title FROM tag WHERE title IN (:tagTitles)";

        return parameterJdbcTemplate.query(
                query, parameters, new BeanPropertyRowMapper<>(Tag.class)
        );
    }

    @Override
    public Map<Long, List<Tag>> findAllByPostIds(List<Long> postIds) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("postIds", postIds);
        String query = """
                SELECT t.id as id, \
                       t.title as title, \
                       pt.post_id as post_id \
                FROM tag as t \
                RIGHT JOIN post_tag as pt ON t.id = pt.tag_id \
                WHERE pt.post_id IN (:postIds);
                """;

        return parameterJdbcTemplate.query(query, paramSource, new TagExtractor());
    }

    @Override
    public void delete(Long tagId) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", tagId);
        String query = "DELETE from tag WHERE id = :id;";

        parameterJdbcTemplate.update(query, paramSource);
    }
}
