package ru.practicum.myblog5.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.myblog5.model.PostTag;

import java.util.List;

@Repository
public interface PostTagRepository extends CrudRepository<PostTag, Void> {

    @Query(value = "SELECT tag_id, post_id FROM post_tag WHERE post_id IN (:postIds);")
    List<PostTag> findAllByPostIds(@Param("postIds") List<Long> postIds);

    @Modifying
    @Query(value = "DELETE FROM post_tag WHERE post_id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
