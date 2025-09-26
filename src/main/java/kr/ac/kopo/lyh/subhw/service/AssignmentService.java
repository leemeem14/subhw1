package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.dto.request.AssignmentCreateDTO;
import kr.ac.kopo.lyh.subhw.dto.response.AssignmentResponseDTO;
import kr.ac.kopo.lyh.subhw.entity.Assignment;
import kr.ac.kopo.lyh.subhw.entity.Course;
import kr.ac.kopo.lyh.subhw.repository.AssignmentRepository;
import kr.ac.kopo.lyh.subhw.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    @PreAuthorize("hasRole('PROFESSOR')")
    public AssignmentResponseDTO createAssignment(AssignmentCreateDTO assignmentDTO, String professorUsername) {
        Course course = courseRepository.findById(assignmentDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));

        // 해당 과목의 교수인지 확인
        if (!course.getProfessor().getUser().getUsername().equals(professorUsername)) {
            throw new RuntimeException("해당 과목의 교수만 과제를 생성할 수 있습니다.");
        }

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(assignmentDTO.getTitle())
                .description(assignmentDTO.getDescription())
                .maxScore(assignmentDTO.getMaxScore())
                .dueDate(assignmentDTO.getDueDate())
                .build();

        assignment = assignmentRepository.save(assignment);
        return new AssignmentResponseDTO(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseIdOrderByDueDate(courseId).stream()
                .map(AssignmentResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByProfessor(String professorUsername) {
        // 교수의 모든 과목의 과제 조회 로직 필요
        return assignmentRepository.findAll().stream()
                .filter(assignment -> assignment.getCourse().getProfessor().getUser().getUsername().equals(professorUsername))
                .map(AssignmentResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDTO getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다."));
        return new AssignmentResponseDTO(assignment);
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    public AssignmentResponseDTO updateAssignment(Long assignmentId, AssignmentCreateDTO assignmentDTO, String professorUsername) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다."));

        // 해당 과목의 교수인지 확인
        if (!assignment.getCourse().getProfessor().getUser().getUsername().equals(professorUsername)) {
            throw new RuntimeException("해당 과목의 교수만 과제를 수정할 수 있습니다.");
        }

        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setMaxScore(assignmentDTO.getMaxScore());
        assignment.setDueDate(assignmentDTO.getDueDate());

        assignment = assignmentRepository.save(assignment);
        return new AssignmentResponseDTO(assignment);
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    public void deleteAssignment(Long assignmentId, String professorUsername) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("과제를 찾을 수 없습니다."));

        // 해당 과목의 교수인지 확인
        if (!assignment.getCourse().getProfessor().getUser().getUsername().equals(professorUsername)) {
            throw new RuntimeException("해당 과목의 교수만 과제를 삭제할 수 있습니다.");
        }

        assignmentRepository.delete(assignment);
    }
}