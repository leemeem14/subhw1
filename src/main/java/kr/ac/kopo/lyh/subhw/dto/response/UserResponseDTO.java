package kr.ac.kopo.lyh.subhw.dto.response;

import kr.ac.kopo.lyh.subhw.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;

    // 교수 정보
    private String department;
    private String position;

    // 학생 정보
    private String studentNumber;
    private String major;
    private Integer grade;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();

        if (user.getProfessor() != null) {
            this.department = user.getProfessor().getDepartment();
            this.position = user.getProfessor().getPosition();
        }

        if (user.getStudent() != null) {
            this.studentNumber = user.getStudent().getStudentNumber();
            this.major = user.getStudent().getMajor();
            this.grade = user.getStudent().getGrade();
        }
    }
}