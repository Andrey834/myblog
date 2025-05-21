package ru.practicum.myblog5.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.myblog5.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query(value = """
            SELECT id, post_id, text, created \
            FROM comment \
            WHERE post_id IN (:ids);
            """)
    List<Comment> findAllByPostIds(@Param("ids") List<Long> postIds);

    List<Comment> findAllByPostId(Long postId);
}
