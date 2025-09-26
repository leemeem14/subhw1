package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.CommentCreateDto;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.CommentService;
import kr.ac.kopo.lyh.subhw.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/create")
    public String createComment(@RequestParam Long postId,
                              @Valid @ModelAttribute CommentCreateDto commentDto,
                              BindingResult result,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 확인해주세요.");
            return "redirect:/posts/" + postId;
        }

        try {
            Post post = postService.getPostById(postId);
            commentService.createComment(commentDto, post, currentUser);
            redirectAttributes.addFlashAttribute("success", "댓글이 작성되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/update/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                              @Valid @ModelAttribute CommentCreateDto commentDto,
                              BindingResult result,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 확인해주세요.");
        } else {
            try {
                commentService.updateComment(commentId, commentDto, currentUser);
                redirectAttributes.addFlashAttribute("success", "댓글이 수정되었습니다.");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }

        // 댓글이 속한 게시글로 리다이렉트하기 위해 댓글 정보 조회
        try {
            Long postId = commentService.getCommentById(commentId).getPost().getId();
            return "redirect:/posts/" + postId;
        } catch (RuntimeException e) {
            return "redirect:/posts";
        }
    }

    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {

        try {
            // 삭제하기 전에 게시글 ID를 미리 가져오기
            Long postId = commentService.getCommentById(commentId).getPost().getId();

            commentService.deleteComment(commentId, currentUser);
            redirectAttributes.addFlashAttribute("success", "댓글이 삭제되었습니다.");

            return "redirect:/posts/" + postId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/posts";
        }
    }
}