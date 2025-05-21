package ru.practicum.myblog5.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.myblog5.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    @Query(value = """
            WITH post_ids AS ( \
                SELECT p.id, p.created  \
                FROM post p \
                LEFT JOIN post_tag as pt ON pt.post_id = p.id \
                LEFT JOIN tag      as  t ON t.id = pt.tag_id \
                WHERE :search = '' OR t.title ILIKE :search \
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
            JOIN post_ids as pi ON pi.id = p.id \
            ORDER BY created desc;
            """)
    List<Post> findByParam(@Param("search") String search,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    @Query(value = """
            SELECT count(ids.*) \
            FROM (SELECT distinct p.id \
                  FROM post as p \
                  LEFT JOIN post_tag as pt ON pt.post_id = p.id \
                  LEFT JOIN tag as t ON t.id = pt.tag_id \
                  WHERE :searchTag = '' OR t.title ILIKE :searchTag \
                  GROUP BY p.id) as ids;""")
    Long countByTag(@Param("searchTag") String tag);

    @Modifying
    @Query(value = "UPDATE post SET likes = likes + :changer WHERE id = :postId")
    void changeLikes(@Param("postId") Long postId,
                     @Param("changer") int changer);
}
