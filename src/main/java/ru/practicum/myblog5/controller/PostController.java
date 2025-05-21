package ru.practicum.myblog5.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.myblog5.dto.Paging;
import ru.practicum.myblog5.dto.PostDto;
import ru.practicum.myblog5.dto.PostShortDto;
import ru.practicum.myblog5.dto.RequestPost;
import ru.practicum.myblog5.enums.ViewName;
import ru.practicum.myblog5.mapper.PostMapper;
import ru.practicum.myblog5.service.PostService;

import java.util.List;

@Controller
@RequestMapping("/posts")
@Slf4j
public class PostController {
    private final PostService postService;
    public static final String REDIRECT_PATH = "redirect:/" + ViewName.POSTS.getValue();

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String viewAllPosts(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                               @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                               Model model, HttpServletRequest httpRequest) {

        log.debug("ENDPOINT: {}; METHOD: {}; IP: {}; viewAllPosts(); search: {}",
                httpRequest.getRequestURI(),
                httpRequest.getMethod(),
                httpRequest.getRemoteAddr(),
                search
        );

        Paging paging = postService.getPaging(search, page, size);
        List<PostShortDto> posts = postService.getAll(paging);

        model.addAttribute("paging", paging);
        model.addAttribute("posts", posts);
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

    @GetMapping("/{id}/edit")
    public String viewEditPost(@PathVariable(name = "id") Long postId, Model model) {
        RequestPost post = PostMapper.toRequest(postService.getById(postId));
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
