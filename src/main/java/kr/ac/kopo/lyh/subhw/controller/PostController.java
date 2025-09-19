package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.CommentCreateDto;
import kr.ac.kopo.lyh.subhw.dto.PostCreateDto;
import kr.ac.kopo.lyh.subhw.entity.FileType;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.CommentService;
import kr.ac.kopo.lyh.subhw.service.FileService;
import kr.ac.kopo.lyh.subhw.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final FileService fileService;

    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;

        if (search != null && !search.trim().isEmpty()) {
            posts = postService.searchPosts(search, pageable);
            model.addAttribute("search", search);
        } else {
            posts = postService.getAllPosts(pageable);
        }

        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new PostCreateDto());
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("post") PostCreateDto postDto,
                             BindingResult result,
                             @RequestParam(value = "files", required = false) MultipartFile[] files,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "posts/create";
        }

        try {
            Post post = postService.createPost(postDto, currentUser);

            // 파일 업로드 처리
            if (files != null) {
                Arrays.stream(files)
                        .filter(file -> !file.isEmpty())
                        .forEach(file -> fileService.saveFile(file, post, currentUser, FileType.ASSIGNMENT_MATERIAL));
            }

            redirectAttributes.addFlashAttribute("success", "게시글이 작성되었습니다.");
            return "redirect:/posts/" + post.getId();
        } catch (Exception e) {
            result.reject("error", "게시글 작성 중 오류가 발생했습니다: " + e.getMessage());
            return "posts/create";
        }
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Post post = postService.getPostById(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments());
        model.addAttribute("files", fileService.getFilesByPost(post));
        model.addAttribute("userFiles", fileService.getFilesByPostAndUploader(post, currentUser));
        model.addAttribute("newComment", new CommentCreateDto());
        model.addAttribute("currentUser", currentUser);

        return "posts/view";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Post post = postService.getPostById(id);

        if (!post.getAuthor().equals(currentUser)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        PostCreateDto postDto = PostCreateDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getType())
                .dueDate(post.getDueDate())
                .build();

        model.addAttribute("post", postDto);
        model.addAttribute("postId", id);
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id,
                           @Valid @ModelAttribute("post") PostCreateDto postDto,
                           BindingResult result,
                           @AuthenticationPrincipal User currentUser,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "posts/edit";
        }

        try {
            postService.updatePost(id, postDto, currentUser);
            redirectAttributes.addFlashAttribute("success", "게시글이 수정되었습니다.");
            return "redirect:/posts/" + id;
        } catch (RuntimeException e) {
            result.reject("error", e.getMessage());
            return "posts/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {

        try {
            postService.deletePost(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "게시글이 삭제되었습니다.");
            return "redirect:/posts";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/posts/" + id;
        }
    }

    // 파일 제출
    @PostMapping("/{id}/submit")
    public String submitFile(@PathVariable Long id,
                             @RequestParam("files") MultipartFile[] files,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {

        Post post = postService.getPostById(id);

        try {
            Arrays.stream(files)
                    .filter(file -> !file.isEmpty())
                    .forEach(file -> fileService.saveFile(file, post, currentUser, FileType.SUBMISSION));

            redirectAttributes.addFlashAttribute("success", "파일이 제출되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "파일 제출 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/posts/" + id;
    }
}
