package ru.yandex.myblog.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.myblog.service.CommentService;

@Controller
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable(name = "postId") Long postId,
                             @RequestParam(name = "message") String message) {
        commentService.addComment(postId, message);
        return PostController.REDIRECT_PATH + "/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String updateComment(@PathVariable(name = "postId") Long postId,
                                @PathVariable(name = "commentId") Long commentId,
                                @RequestParam(name = "message") String message) {
        commentService.updateComment(postId, commentId, message);
        return PostController.REDIRECT_PATH + "/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable(name = "postId") Long postId,
                                @PathVariable(name = "commentId") Long commentId) {
        commentService.deleteComment(postId, commentId);
        return PostController.REDIRECT_PATH + "/" + postId;
    }
}
