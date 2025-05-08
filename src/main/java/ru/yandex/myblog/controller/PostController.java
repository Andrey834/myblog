package ru.yandex.myblog.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.myblog.dto.Paging;
import ru.yandex.myblog.dto.PostDto;
import ru.yandex.myblog.dto.PostShortDto;
import ru.yandex.myblog.dto.RequestPost;
import ru.yandex.myblog.enums.ViewName;
import ru.yandex.myblog.mapper.PostMapper;
import ru.yandex.myblog.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    public static final String REDIRECT_PATH = "redirect:/posts";

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    String viewPosts(@RequestParam(required = false, name = "search", defaultValue = "") String search,
                     @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                     @RequestParam(required = false, name = "size", defaultValue = "10") Integer size,
                     Model model) {
        int[] pageSizes = {5, 10, 20, 50};
        Paging paging = new Paging(page, size, search, 0, pageSizes);
        paging = postService.assignMaxPage(paging);

        List<PostShortDto> posts = postService.getAllByParam(paging);

        model.addAttribute("posts", posts);
        model.addAttribute("paging", paging);
        return ViewName.POSTS.getValue();
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable(name = "id") Long postId, Model model) {
        PostDto post = postService.getById(postId);
        model.addAttribute("post", post);
        return ViewName.POST.getValue();
    }

    @GetMapping("/add")
    public String viewPublish(Model model) {
        model.addAttribute("post", new RequestPost());
        return ViewName.PUBLISH.getValue();
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String processAddPost(@Valid @ModelAttribute(name = "post") RequestPost post,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return ViewName.PUBLISH.getValue();
        }

        Long postId = postService.create(post);
        return REDIRECT_PATH + "/" + postId;
    }

    //task
    @GetMapping("/{id}/edit")
    public String viewEditPost(@PathVariable(name = "id") Long postId, Model model) {
        RequestPost post = PostMapper.toRequestPost(postService.getById(postId));
        model.addAttribute("post", post);
        return ViewName.PUBLISH.getValue();
    }

    @PostMapping("/{id}/edit")
    public String processEditPost(@PathVariable(name = "id") Long postId,
                                  @Valid @ModelAttribute(name = "post") RequestPost post,
                                  Model model) {
        postService.update(postId, post);
        model.addAttribute("id", postId);
        return REDIRECT_PATH + "/" + postId;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long postId) {
        postService.remove(postId);
        return REDIRECT_PATH;
    }

    @PostMapping("/{id}/like")
    public String processAddLike(@PathVariable(name = "id") Long postId,
                                 @RequestParam(name = "like") boolean like) {
        postService.addLike(postId, like);
        return REDIRECT_PATH + "/" + postId;
    }
}
