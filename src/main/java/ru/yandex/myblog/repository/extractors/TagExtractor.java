package ru.yandex.myblog.repository.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.myblog.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TagExtractor implements ResultSetExtractor<Map<Long, List<Tag>>> {

    @Override
    public Map<Long, List<Tag>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, List<Tag>> map = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");
            String title = rs.getString("title");
            Long postId = rs.getLong("post_id");

            Tag tag = Tag.builder()
                    .id(id)
                    .title(title)
                    .build();

            map.putIfAbsent(postId, new ArrayList<>());
            map.get(postId).add(tag);
        }
        return map;
    }
}
