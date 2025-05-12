package ru.yandex.myblog.repository.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.myblog.model.Comment;
import ru.yandex.myblog.repository.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public JdbcNativeCommentRepository(NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    @Override
    public void save(Comment comment) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postId", comment.getPostId());
        parameters.addValue("text", comment.getText());
        parameters.addValue("created", comment.getCreated());

        String query = """
                INSERT INTO comment(post_id, text, created) \
                VALUES (:postId, :text, :created);
                """;

        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public void update(Comment comment) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", comment.getId());
        parameters.addValue("postId", comment.getPostId());
        parameters.addValue("text", comment.getText());

        String query = """
                UPDATE comment SET text = :text \
                WHERE id = :id;
                """;

        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        String query = "SELECT id, post_id, text, created FROM comment WHERE id = :id";


        Comment comment;
        try {
            comment = parameterJdbcTemplate.queryForObject(
                    query,
                    parameters,
                    BeanPropertyRowMapper.newInstance(Comment.class)
            );
        } catch (EmptyResultDataAccessException e) {
            comment = null;
        }

        return Optional.ofNullable(comment);
    }

    @Override
    public void delete(Long postId, Long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postId", postId);
        parameters.addValue("commentId", id);

        String query = "DELETE FROM comment WHERE id = :commentId AND post_id = :postId;";
        parameterJdbcTemplate.update(query, parameters);
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postId", postId);

        String query = """
                SELECT id, post_id, text, created \
                FROM comment WHERE post_id = :postId \
                ORDER BY created DESC;
                """;

        return parameterJdbcTemplate.query(
                query,
                parameters,
                new BeanPropertyRowMapper<>(Comment.class)
        );
    }

    @Override
    public List<Comment> findAllByPostIds(Set<Long> postIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("postIds", postIds);

        String query = """
                SELECT id, post_id, text, created \
                FROM comment WHERE post_id IN (:postIds) \
                ORDER BY created DESC;
                """;

        return parameterJdbcTemplate.query(
                query,
                parameters,
                new BeanPropertyRowMapper<>(Comment.class)
        );
    }
}
