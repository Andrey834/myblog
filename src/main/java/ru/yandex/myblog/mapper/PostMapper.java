package ru.yandex.myblog.mapper;

import ru.yandex.myblog.dto.CommentDto;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;
import ru.yandex.myblog.dto.TagDto;
import ru.yandex.myblog.enums.DateTimeFormat;
import ru.yandex.myblog.model.Post;
import ru.yandex.myblog.model.Tag;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class PostMapper {

    private PostMapper() {
    }

    public static Post requestToPost(RequestPost requestPost, String imageName) {
        List<Tag> tags = Arrays.stream(requestPost.getTags().split(" "))
                .map(Tag::new)
                .toList();

        return Post.builder()
                .title(requestPost.getTitle())
                .text(requestPost.getText())
                .image(imageName)
                .created(LocalDateTime.now())
                .tags(tags)
                .likes(0L)
                .build();
    }

    public static RequestPost toRequestPost(PostDto postDto) {
        StringBuilder text = new StringBuilder();
        for (var i = 0; i < postDto.textParts().length; i++) {
            text.append(postDto.textParts()[i]).append("\n");
        }

        StringBuilder strTags = new StringBuilder();
        postDto.tags().forEach(tag -> strTags.append(tag.title()).append(" "));

        return RequestPost.builder()
                .title(postDto.title())
                .text(text.toString())
                .tags(strTags.toString())
                .build();
    }

    public static PostDto toPostDto(Post post) {
        List<CommentDto> comments = post.getComments().stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        List<TagDto> tags = post.getTags().stream()
                .map(TagMapper::toTagDto)
                .toList();

        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .textParts(post.getTextParts())
                .likes(post.getLikes())
                .created(post.getCreated().format(DateTimeFormat.FOR_VIEW_FULL.getFormatter()))
                .image(post.getImage())
                .tags(tags)
                .comments(comments)
                .build();
    }

    public static PostShortDto toPostShortDto(Post post) {
        List<String> strTags = post.getTags().stream().map(Tag::getTitle).toList();
        int commentsCount = post.getComments().size();

        return PostShortDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .preview(post.getTextParts()[0])
                .image(post.getImage())
                .created(post.getCreated().format(DateTimeFormat.FOR_VIEW_FULL.getFormatter()))
                .likes(post.getLikes())
                .tags(strTags)
                .countComments(commentsCount)
                .build();
    }
}
