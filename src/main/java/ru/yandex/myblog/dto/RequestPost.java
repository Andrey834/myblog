package ru.yandex.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RequestPost {
    @Size(min=2, max=250, message = "Post.Title: minimum 2 and maximum 250 characters")
    @NotBlank(message = "Post: Title is required")
    private String title;
    @Size(min=10, max=5000, message = "Post.text: minimum 2 and maximum 1500 characters")
    @NotBlank(message = "Post: text is required")
    private String text;
    @NotBlank(message = "Post: Tags is required")
    private String tags;
    @NotNull(message = "Post: Image is required")
    private MultipartFile image;
}
