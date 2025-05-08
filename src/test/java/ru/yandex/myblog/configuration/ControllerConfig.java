package ru.yandex.myblog.configuration;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.myblog.controller.CommentController;
import ru.yandex.myblog.controller.ImageController;
import ru.yandex.myblog.controller.PostController;
import ru.yandex.myblog.repository.CommentRepository;
import ru.yandex.myblog.repository.PostRepository;
import ru.yandex.myblog.repository.PostTagRepository;
import ru.yandex.myblog.repository.TagRepository;
import ru.yandex.myblog.repository.impl.JdbcNativeCommentRepository;
import ru.yandex.myblog.repository.impl.JdbcNativePostRepository;
import ru.yandex.myblog.repository.impl.JdbcNativePostTagRepository;
import ru.yandex.myblog.repository.impl.JdbcNativeTagRepository;
import ru.yandex.myblog.service.CommentService;
import ru.yandex.myblog.service.ImageService;
import ru.yandex.myblog.service.PostService;
import ru.yandex.myblog.service.TagService;
import ru.yandex.myblog.service.impl.CommentServiceImpl;
import ru.yandex.myblog.service.impl.ImageServiceImpl;
import ru.yandex.myblog.service.impl.PostServiceImpl;
import ru.yandex.myblog.service.impl.TagServiceImpl;
import ru.yandex.myblog.service.validator.PostValidator;

@Configuration
@TestPropertySource(locations = "classpath:test-application.properties")
@ComponentScan(value = "ru.yandex.myblog")
public class ControllerConfig {

   /* @Bean
    public ImageService mockImageService() {
        return Mockito.mock(ImageServiceImpl.class);
    }

    @Bean
    public PostRepository postRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcNativePostRepository(jdbcTemplate);
    }

    @Bean
    public TagRepository tagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcNativeTagRepository(jdbcTemplate);
    }

    @Bean
    public PostTagRepository postTagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcNativePostTagRepository(jdbcTemplate);
    }

    @Bean
    public CommentRepository commentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcNativeCommentRepository(jdbcTemplate);
    }

    @Bean
    public TagService tagService(TagRepository tagRepository, PostTagRepository postTagRepository) {
        return new TagServiceImpl(tagRepository, postTagRepository);
    }

    @Bean
    public CommentService commentService(CommentRepository commentRepository) {
        return new CommentServiceImpl(commentRepository);
    }

    @Bean
    public PostService postService(PostRepository postRepository,
                                   @Qualifier("mockImageService") ImageService imageService,
                                   TagService tagService,
                                   CommentService commentService,
                                   PostValidator postValidator) {
        return new PostServiceImpl(postRepository, imageService, tagService ,  commentService, postValidator);
    }

    @Bean
    public PostValidator postValidator() {
        return new PostValidator();
    }*/

   /* @Bean
    @Primary
    public CommentController commentController(CommentService commentService) {
        return new CommentController(commentService);
    }

    @Bean
    @Primary
    public PostController postController(PostService postService) {
        return new PostController(postService);
    }

    @Bean
    @Primary
    public ImageController imageController(ImageService imageService) {
        return new ImageController(imageService);
    }*/
}
