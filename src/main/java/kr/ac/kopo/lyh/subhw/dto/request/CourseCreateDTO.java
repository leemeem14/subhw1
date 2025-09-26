package kr.ac.kopo.lyh.subhw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateDTO {

    @NotBlank(message = "과목 코드는 필수입니다")
    @Size(max = 20, message = "과목 코드는 20자를 초과할 수 없습니다")
    private String courseCode;

    @NotBlank(message = "과목명은 필수입니다")
    @Size(max = 100, message = "과목명은 100자를 초과할 수 없습니다")
    private String courseName;

    @NotBlank(message = "학기는 필수입니다")
    private String semester;

    @NotNull(message = "년도는 필수입니다")
    private Integer year;

    private String description;
}