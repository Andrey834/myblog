package ru.yandex.myblog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = "id")
@Table(name = "post")
public class Tag {
    @Id
    private Long id;
    private String title;

    public Tag(String title) {
        this.title = title;
    }
}
