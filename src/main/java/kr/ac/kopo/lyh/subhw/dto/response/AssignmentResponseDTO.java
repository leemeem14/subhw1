package kr.ac.kopo.lyh.subhw.dto.response;

import kr.ac.kopo.lyh.subhw.entity.Assignment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AssignmentResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Integer maxScore;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private Long courseId;
    private String courseName;
    private String professorName;
    private Integer submissionCount;
    private Boolean isOverdue;

    public AssignmentResponseDTO(Assignment assignment) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.maxScore = assignment.getMaxScore();
        this.dueDate = assignment.getDueDate();
        this.createdAt = assignment.getCreatedAt();

        if (assignment.getCourse() != null) {
            this.courseId = assignment.getCourse().getId();
            this.courseName = assignment.getCourse().getCourseName();
            if (assignment.getCourse().getProfessor() != null) {
                this.professorName = assignment.getCourse().getProfessor().getUser().getUsername();
            }
        }

        this.submissionCount = assignment.getSubmissions() != null ? assignment.getSubmissions().size() : 0;
        this.isOverdue = LocalDateTime.now().isAfter(assignment.getDueDate());
    }
}