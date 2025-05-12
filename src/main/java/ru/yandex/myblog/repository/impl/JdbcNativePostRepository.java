package ru.yandex.myblog.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.repository.extractors.PostsExtractor;
import ru.yandex.myblog.repository.PostRepository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcNativePostRepository implements PostRepository {
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public JdbcNativePostRepository(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public Long save(Post post) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", post.getTitle());
        parameters.addValue("text", post.getText());
        parameters.addValue("image", post.getImage());
        parameters.addValue("created", post.getCreated());
        parameters.addValue("likes", post.getLikes());

        String query = """
                       INSERT INTO post(title, text, image, created, likes) \
                       VALUES (:title, :text, :image, :created, :likes) \
                       RETURNING id;
                       """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameterJdbcTemplate.update(query, parameters, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void update(Post post) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", post.getId());
        parameters.addValue("title", post.getTitle());
        parameters.addValue("text", post.getText());
        parameters.addValue("image", post.getImage());

        String query = """
                    UPDATE post \
                    SET title = :title, text = :text, image = :image \
                    WHERE id = :id;
                    """;

        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public void delete(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        String query = "DELETE FROM post WHERE id = :id;";
        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public void changeLikes(Long postId, int changer) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postId", postId);
        parameters.addValue("changer", changer);

        String query = "UPDATE post SET likes = likes + :changer WHERE id = :postId;";
        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public Optional<Post> findById(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        String query = """
                SELECT p.id       as id, \
                       p.title    as title, \
                       p.text     as text, \
                       p.image    as image, \
                       p.created  as created, \
                       p.likes    as likes \
                FROM post as p \
                WHERE p.id = :id;
                """;

        Post post = parameterJdbcTemplate.queryForObject(
                query, parameters, BeanPropertyRowMapper.newInstance(Post.class));
        return Optional.ofNullable(post);
    }

    @Override
    public Map<Long, Post> findAll(Paging paging) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("searchTag", paging.getSearch());
        parameters.addValue("limit", paging.getSize());
        parameters.addValue("offset", (paging.getPage() - 1) * paging.getSize());

        String query = """
                WITH post_ids AS ( \
                    SELECT p.id, p.created \
                    FROM post p \
                    LEFT JOIN post_tag as pt ON pt.post_id = p.id \
                    LEFT JOIN tag      as  t ON t.id = pt.tag_id \
                    WHERE :searchTag = '' OR t.title ILIKE :searchTag \
                    GROUP BY p.id, p.created \
                    ORDER BY p.created desc \
                    OFFSET :offset \
                    LIMIT :limit \
                ) \
                SELECT p.id          as id, \
                       p.title       as title, \
                       p.text        as text, \
                       p.image       as image, \
                       p.created     as created, \
                       p.likes       as likes \
                FROM post as p \
                WHERE p.id IN (SELECT id FROM post_ids) \
                ORDER BY created desc;
                """;

        return parameterJdbcTemplate.query(query, parameters, new PostsExtractor());
    }

    @Override
    public Long quantityByTag(String searchTag) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("searchTag", searchTag);

        String query = """
                SELECT count(ids.*) \
                FROM (SELECT distinct p.id \
                      FROM post as p \
                      LEFT JOIN post_tag as pt ON pt.post_id = p.id \
                      LEFT JOIN tag as t ON t.id = pt.tag_id \
                      WHERE :searchTag = '' OR t.title ILIKE :searchTag \
                      GROUP BY p.id) as ids;
                """;

        return parameterJdbcTemplate.queryForObject(query, parameters, Long.class);
    }
}
