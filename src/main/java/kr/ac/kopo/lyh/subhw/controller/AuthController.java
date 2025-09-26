package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.UserRegistrationDto;
import kr.ac.kopo.lyh.subhw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        // 이전 페이지 정보를 저장 (로그인 후 리다이렉트용)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
            request.getSession().setAttribute("prevPage", referer);
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * 로그아웃 성공 후 호출되는 핸들러
     * Spring Security가 자동으로 /logout을 처리하므로
     * 별도의 POST 메서드는 불필요하지만,
     * 로그아웃 후 추가 처리가 필요한 경우 사용
     */
    @GetMapping("/logout-success")
    public String logoutSuccess(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("info", "성공적으로 로그아웃되었습니다.");
        return "redirect:/";
    }
}