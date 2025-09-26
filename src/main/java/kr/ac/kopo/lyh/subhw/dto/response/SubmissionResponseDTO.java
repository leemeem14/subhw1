package kr.ac.kopo.lyh.subhw.dto.response;

import kr.ac.kopo.lyh.subhw.entity.Submission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SubmissionResponseDTO {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private String studentName;
    private String studentNumber;
    private String fileName;
    private String content;
    private Integer score;
    private Submission.Status status;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private Integer commentCount;
    private Boolean isLate;

    public SubmissionResponseDTO(Submission submission) {
        this.id = submission.getId();
        this.fileName = submission.getFileName();
        this.content = submission.getContent();
        this.score = submission.getScore();
        this.status = submission.getStatus();
        this.submittedAt = submission.getSubmittedAt();
        this.gradedAt = submission.getGradedAt();

        if (submission.getAssignment() != null) {
            this.assignmentId = submission.getAssignment().getId();
            this.assignmentTitle = submission.getAssignment().getTitle();
            this.isLate = submission.getSubmittedAt().isAfter(submission.getAssignment().getDueDate());
        }

        if (submission.getStudent() != null) {
            this.studentName = submission.getStudent().getUser().getUsername();
            this.studentNumber = submission.getStudent().getStudentNumber();
        }

        this.commentCount = submission.getComments() != null ? submission.getComments().size() : 0;
    }
}