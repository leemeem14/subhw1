package kr.ac.kopo.lyh.subhw.dto.response;

import kr.ac.kopo.lyh.subhw.entity.Course;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CourseResponseDTO {

    private Long id;
    private String courseCode;
    private String courseName;
    private String professorName;
    private String department;
    private String semester;
    private Integer year;
    private String description;
    private LocalDateTime createdAt;
    private Integer enrollmentCount;
    private Integer assignmentCount;

    public CourseResponseDTO(Course course) {
        this.id = course.getId();
        this.courseCode = course.getCourseCode();
        this.courseName = course.getCourseName();
        this.semester = course.getSemester();
        this.year = course.getYear();
        this.description = course.getDescription();
        this.createdAt = course.getCreatedAt();

        if (course.getProfessor() != null) {
            this.professorName = course.getProfessor().getUser().getUsername();
            this.department = course.getProfessor().getDepartment();
        }

        this.enrollmentCount = course.getStudents() != null ? course.getStudents().size() : 0;
        this.assignmentCount = course.getAssignments() != null ? course.getAssignments().size() : 0;
    }
}