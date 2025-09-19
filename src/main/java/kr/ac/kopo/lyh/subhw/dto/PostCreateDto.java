package kr.ac.kopo.lyh.subhw.dto;

import kr.ac.kopo.lyh.subhw.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateDto {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Builder.Default
    private PostType type = PostType.ASSIGNMENT;

    private LocalDateTime dueDate;
}
