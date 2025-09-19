package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.PostService;
import kr.ac.kopo.lyh.subhw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping
    public String profile(@AuthenticationPrincipal User currentUser, Model model) {
        // 사용자의 최근 게시글 5개 가져오기
        Pageable pageable = PageRequest.of(0, 5);
        model.addAttribute("user", currentUser);
        model.addAttribute("recentPosts", postService.getPostsByAuthor(currentUser, pageable));
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("user", currentUser);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String editProfile(@RequestParam String realName,
                              @RequestParam String phoneNumber,
                              @RequestParam(required = false) MultipartFile profileImage,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {

        try {
            userService.updateProfile(currentUser.getId(), realName, phoneNumber, profileImage);
            redirectAttributes.addFlashAttribute("success", "프로필이 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal User currentUser,
                                 RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/change-password";
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "비밀번호는 최소 6자 이상이어야 합니다.");
            return "redirect:/profile/change-password";
        }

        try {
            userService.changePassword(currentUser.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다.");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/change-password";
        }
    }
}
