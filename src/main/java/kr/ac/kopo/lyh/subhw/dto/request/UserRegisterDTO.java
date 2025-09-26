package kr.ac.kopo.lyh.subhw.dto.request;

import kr.ac.kopo.lyh.subhw.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3-50자 사이여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotNull(message = "역할을 선택해주세요")
    private User.Role role;

    // 교수 전용 필드
    private String department;
    private String position;

    // 학생 전용 필드
    private String studentNumber;
    private String major;
    private Integer grade;
}