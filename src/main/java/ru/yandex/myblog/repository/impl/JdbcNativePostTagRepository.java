package ru.yandex.myblog.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.yandex.myblog.model.PostTag;
import ru.yandex.myblog.repository.PostTagRepository;
import java.util.List;

@Repository
public class JdbcNativePostTagRepository implements PostTagRepository {
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public JdbcNativePostTagRepository(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public void saveAll(List<PostTag> postTags) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(postTags.toArray());
        String sql = """
                INSERT INTO post_tag (post_id, tag_id) \
                VALUES (:postId, :tagId) \
                ON CONFLICT DO NOTHING;
                """;

        parameterJdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public List<PostTag> findAllByPostId(Long postId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postId", postId);
        String query = "SELECT post_id, tag_id FROM post_tag WHERE post_id = :postId;";

        return parameterJdbcTemplate.query(
                query,
                parameters,
                new BeanPropertyRowMapper<>(PostTag.class)
        );
    }

    @Override
    public void removeAllByPostId(Long postId) {
        String query = "DELETE FROM post_tag WHERE post_id = ?;";
        parameterJdbcTemplate.getJdbcTemplate()
                .update(query, postId);
    }
}
