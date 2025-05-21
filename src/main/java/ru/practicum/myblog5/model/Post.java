package ru.practicum.myblog5.model;

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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post")
public class Post {
    @Id
    private Long id;
    private String title;
    private String text;
    private String image;
    private Long likes;
    private LocalDateTime created;

    @Transient
    private List<Comment> comments = new ArrayList<>();
    @Transient
    private List<Tag> tags = new ArrayList<>();

    public String[] getTextParts() {
        return this.text.split("\\n+");
    }
}
