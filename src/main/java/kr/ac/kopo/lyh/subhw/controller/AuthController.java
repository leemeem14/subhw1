package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.request.LoginDTO;
import kr.ac.kopo.lyh.subhw.dto.request.UserRegisterDTO;
import kr.ac.kopo.lyh.subhw.dto.response.UserResponseDTO;
import kr.ac.kopo.lyh.subhw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (logout != null) {
            model.addAttribute("message", "성공적으로 로그아웃되었습니다.");
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDTO userRegisterDTO,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(userRegisterDTO);
            model.addAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        UserResponseDTO user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);

        // 역할에 따라 다른 대시보드로 리다이렉트
        if (user.getRole().name().equals("PROFESSOR")) {
            return "redirect:/professor/dashboard";
        } else if (user.getRole().name().equals("STUDENT")) {
            return "redirect:/student/dashboard";
        }

        return "dashboard/index";
    }

    // REST API Endpoints
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerApi(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            UserResponseDTO user = userService.register(userRegisterDTO);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserResponseDTO user = userService.findByUsername(loginDTO.getUsername());

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인에 실패했습니다.");
        }
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.unauthorized().build();
        }

        UserResponseDTO user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }
}