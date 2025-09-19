package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.CommentCreateDto;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.CommentService;
import kr.ac.kopo.lyh.subhw.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

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
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 입력해주세요.");
            return "redirect:/posts/" + postId;
        }

        try {
            Post post = postService.getPostById(postId);
            commentService.createComment(commentDto, post, currentUser);
            redirectAttributes.addFlashAttribute("success", "댓글이 작성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "댓글 작성 중 오류가 발생했습니다.");
        }

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{id}/edit")
    public String editComment(@PathVariable Long id,
                              @RequestParam String content,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {

        try {
            CommentCreateDto dto = new CommentCreateDto();
            dto.setContent(content);
            commentService.updateComment(id, dto, currentUser);
            redirectAttributes.addFlashAttribute("success", "댓글이 수정되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // 원래 게시글로 리다이렉트하기 위해 댓글의 게시글 ID를 가져와야 함
        // 여기서는 간단히 처리하기 위해 referer를 사용하거나,
        // CommentService에서 Post 정보를 반환하도록 수정할 수 있음
        return "redirect:/posts";
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id,
                                @RequestParam Long postId,
                                @AuthenticationPrincipal User currentUser,
                                RedirectAttributes redirectAttributes) {

        try {
            commentService.deleteComment(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "댓글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/posts/" + postId;
    }
}
