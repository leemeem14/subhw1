package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.dto.request.CourseCreateDTO;
import kr.ac.kopo.lyh.subhw.dto.response.CourseResponseDTO;
import kr.ac.kopo.lyh.subhw.entity.Course;
import kr.ac.kopo.lyh.subhw.entity.Professor;
import kr.ac.kopo.lyh.subhw.entity.Student;
import kr.ac.kopo.lyh.subhw.repository.CourseRepository;
import kr.ac.kopo.lyh.subhw.repository.ProfessorRepository;
import kr.ac.kopo.lyh.subhw.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    @PreAuthorize("hasRole('PROFESSOR')")
    public CourseResponseDTO createCourse(CourseCreateDTO courseDTO, String professorUsername) {
        // 중복 검사
        if (courseRepository.existsByCourseCode(courseDTO.getCourseCode())) {
            throw new RuntimeException("이미 존재하는 과목 코드입니다.");
        }

        Professor professor = professorRepository.findByUsername(professorUsername)
                .orElseThrow(() -> new RuntimeException("교수 정보를 찾을 수 없습니다."));

        Course course = Course.builder()
                .courseCode(courseDTO.getCourseCode())
                .courseName(courseDTO.getCourseName())
                .professor(professor)
                .semester(courseDTO.getSemester())
                .year(courseDTO.getYear())
                .description(courseDTO.getDescription())
                .build();

        course = courseRepository.save(course);
        return new CourseResponseDTO(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByProfessor(String professorUsername) {
        Professor professor = professorRepository.findByUsername(professorUsername)
                .orElseThrow(() -> new RuntimeException("교수 정보를 찾을 수 없습니다."));

        return courseRepository.findByProfessor(professor).stream()
                .map(CourseResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByStudent(String studentUsername) {
        Student student = studentRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));

        return courseRepository.findByStudentId(student.getId()).stream()
                .map(CourseResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));
        return new CourseResponseDTO(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public void enrollInCourse(Long courseId, String studentUsername) {
        Course course = courseRepository.findByIdWithStudents(courseId)
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));

        Student student = studentRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("학생 정보를 찾을 수 없습니다."));

        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
            courseRepository.save(course);
        } else {
            throw new RuntimeException("이미 수강 신청한 과목입니다.");
        }
    }

    @PreAuthorize("hasRole('PROFESSOR') and @courseService.isProfessorOfCourse(#courseId, authentication.name)")
    public CourseResponseDTO updateCourse(Long courseId, CourseCreateDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));

        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseName(courseDTO.getCourseName());
        course.setSemester(courseDTO.getSemester());
        course.setYear(courseDTO.getYear());
        course.setDescription(courseDTO.getDescription());

        course = courseRepository.save(course);
        return new CourseResponseDTO(course);
    }

    @PreAuthorize("hasRole('PROFESSOR') and @courseService.isProfessorOfCourse(#courseId, authentication.name)")
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("과목을 찾을 수 없습니다."));
        courseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public boolean isProfessorOfCourse(Long courseId, String professorUsername) {
        return courseRepository.findById(courseId)
                .map(course -> course.getProfessor().getUser().getUsername().equals(professorUsername))
                .orElse(false);
    }
}