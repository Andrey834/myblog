package ru.yandex.myblog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "post")
public class Post {
    @Id
    private Long id;
    private String title;
    private String text;
    private String image;
    private LocalDateTime created;
    private Long likes;
    @Transient
    private List<Tag> tags;
    @Transient
    private List<Comment> comments;

    public String[] getTextParts() {
        return this.text.split("\\n+");
    }
}
