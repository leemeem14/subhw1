package kr.ac.kopo.lyh.subhw.dto;

import kr.ac.kopo.lyh.subhw.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 4, max = 20, message = "사용자명은 4~20자 사이여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 30, message = "비밀번호는 6~30자 사이여야 합니다")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "실명은 필수입니다")
    private String realName;

    private String phoneNumber;

    @Builder.Default
    private Role role = Role.STUDENT;
}