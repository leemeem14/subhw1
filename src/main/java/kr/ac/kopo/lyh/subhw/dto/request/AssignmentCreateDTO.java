package kr.ac.kopo.lyh.subhw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AssignmentCreateDTO {

    @NotBlank(message = "과제 제목은 필수입니다")
    private String title;

    @NotBlank(message = "과제 설명은 필수입니다")
    private String description;

    @Min(value = 1, message = "최대 점수는 1점 이상이어야 합니다")
    @Max(value = 1000, message = "최대 점수는 1000점을 초과할 수 없습니다")
    private Integer maxScore = 100;

    @NotNull(message = "마감일은 필수입니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;

    @NotNull(message = "과목을 선택해주세요")
    private Long courseId;
}