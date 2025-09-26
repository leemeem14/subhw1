package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@RequestParam @NotBlank String realName,
                              @RequestParam String phoneNumber,
                              @RequestParam(required = false) MultipartFile profileImage,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(currentUser.getId(), realName, phoneNumber, profileImage);
            redirectAttributes.addFlashAttribute("success", "프로필이 수정되었습니다.");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam @NotBlank String currentPassword,
                               @RequestParam @NotBlank String newPassword,
                               @RequestParam @NotBlank String confirmPassword,
                               @AuthenticationPrincipal User currentUser,
                               RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호와 확인이 일치하지 않습니다.");
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