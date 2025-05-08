package ru.yandex.myblog.configuration;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.yandex.myblog.repository.CommentRepository;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.service.validator.PostValidator;

@Configuration
public class ServiceConfig {

    @Bean
    @Profile("unit-test")
    public PostRepository mockPostRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    @Profile("unit-test")
    public CommentRepository mockCommentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    @Profile("unit-test")
    public TagRepository mockTagRepository() {
        return Mockito.mock(TagRepository.class);
    }

    @Bean
    @Profile("unit-test")
    public PostTagRepository mockPostTagRepository() {
        return Mockito.mock(PostTagRepository.class);
    }

    @Bean
    @Profile("unit-test")
    public PostValidator postValidator() {
        return new PostValidator();
    }
}
