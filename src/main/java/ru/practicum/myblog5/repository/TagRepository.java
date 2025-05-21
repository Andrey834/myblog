package ru.practicum.myblog5.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.myblog5.model.Tag;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT id, title FROM tag WHERE title IN (:titles)")
    List<Tag> findAllByTitle(@Param("titles") List<String> titles);

    @Query(value = """
            SELECT t.id, t.title \
            FROM tag as t \
            LEFT JOIN post_tag as pt ON t.id = pt.tag_id \
            WHERE pt.post_id = :postId;
            """)
    List<Tag> findAllByPostId(@Param("postId") Long postId);

    @Query(value = "SELECT id, title FROM tag WHERE id IN (:tagIds);")
    List<Tag> findAllByIds(@Param("tagIds") List<Long> tagIds);
}
