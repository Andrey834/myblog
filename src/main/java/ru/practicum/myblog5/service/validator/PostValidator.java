package ru.practicum.myblog5.service.validator;

import org.springframework.stereotype.Component;
import ru.practicum.myblog5.dto.RequestPost;

@Component
public class PostValidator {

    public void requestValidate(RequestPost post) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Post Title cannot be null or empty");
        } else if (post.getTitle().length() < 3 || post.getTitle().length() > 250) {
            throw new IllegalArgumentException("Post Title minimum 2 and maximum 250 characters");
        }

        if (post.getText() == null || post.getText().isEmpty()) {
            throw new IllegalArgumentException("Post Text cannot be null or empty");
        } else if (post.getText().length() < 10 || post.getText().length() > 5000) {
            throw new IllegalArgumentException("Post Text minimum 10 and maximum 5000 characters");
        }

        if (post.getTags() == null || post.getTags() .isEmpty()) {
            throw new IllegalArgumentException("Post Tags cannot be null or empty");
        }

        if (post.getImage() == null || post.getImage().isEmpty()) {
            throw new IllegalArgumentException("Post Image cannot be null or empty");
        }
    }
}
