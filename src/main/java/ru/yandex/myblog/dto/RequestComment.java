package ru.yandex.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

public record RequestComment(
        @NonNull
        @NotBlank
        String message
) {
}
