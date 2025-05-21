package ru.practicum.myblog5.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.myblog5.dto.RequestPost;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PostValidator.class)
class PostValidatorTest {
    @Autowired
    private PostValidator validator;
    private RequestPost requestPost;

    @BeforeEach
    void setUp() {
        requestPost = RequestPost.builder()
                .title("title")
                .text("texttexttexttexttext")
                .tags("new old")
                .image(getTestFile())
                .build();
    }

    @Test
    @DisplayName("PostValidator success")
    void testRequestValidate_success() {
        assertDoesNotThrow(() -> validator.requestValidate(requestPost));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFieldsInRequestPost")
    @DisplayName("PostValidator: Invalid fields -> exception")
    void testRequestValidate_whenEmptyTitle_exception(RequestPost requestPostInvalid) {

        assertThrows(IllegalArgumentException.class,
                () -> validator.requestValidate(requestPostInvalid));
    }

    private static MultipartFile getTestFile() {
        return new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );
    }

    private static Stream<RequestPost> provideInvalidFieldsInRequestPost() {
        String longTitle = "a".repeat(251);
        String longText = "t".repeat(5001);

        return Stream.of(
                new RequestPost(),
                new RequestPost("", "test test test", "sd gf rd", getTestFile()),
                new RequestPost("TT", "test test test", "sd gf rd", getTestFile()),
                new RequestPost(longTitle, "test test test", "sd gf rd", getTestFile()),
                new RequestPost(null, "test test test", "sd gf rd", getTestFile()),
                new RequestPost("Test", "", "sd gf rd", getTestFile()),
                new RequestPost("Test", "testtest", "sd gf rd", getTestFile()),
                new RequestPost("Test", longText, "sd gf rd", getTestFile()),
                new RequestPost("Test", null, "sd gf rd", getTestFile()),
                new RequestPost("Test", "test test test", "", getTestFile()),
                new RequestPost("Test", "test test test", null, getTestFile()),
                new RequestPost("Test", "test test test", "sd gf rd", null)
        );
    }
}
