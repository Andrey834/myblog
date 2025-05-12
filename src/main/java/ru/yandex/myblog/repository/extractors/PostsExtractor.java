package ru.yandex.myblog.repository.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.myblog.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostsExtractor implements ResultSetExtractor<Map<Long, Post>> {

    @Override
    public Map<Long, Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Post> map = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");
            String title = rs.getString("title");
            String content = rs.getString("text");
            String image = rs.getString("image");
            LocalDateTime created = rs.getTimestamp("created").toLocalDateTime();
            Long likes = rs.getLong("likes");

            Post post = Post.builder()
                    .id(id)
                    .title(title)
                    .text(content)
                    .image(image)
                    .created(created)
                    .likes(likes)
                    .tags(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .build();

            map.putIfAbsent(id, post);
        }
        return map;
    }
}
