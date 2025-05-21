package ru.practicum.myblog5.controller;

import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.myblog5.service.CommentService;

@Controller
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable(name = "postId") Long postId,
                             @RequestParam(name = "message") @Size(min = 2, max = 500) String message,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
        }

        commentService.create(postId, message);
        return PostController.REDIRECT_PATH + "/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String updateComment(@PathVariable(name = "postId") Long postId,
                                @PathVariable(name = "commentId") Long commentId,
                                @RequestParam(name = "message") String message) {
        commentService.update(postId, commentId, message);
        return PostController.REDIRECT_PATH + "/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable(name = "postId") Long postId,
                                @PathVariable(name = "commentId") Long commentId) {
        commentService.delete(postId, commentId);
        return PostController.REDIRECT_PATH + "/" + postId;
    }
}
