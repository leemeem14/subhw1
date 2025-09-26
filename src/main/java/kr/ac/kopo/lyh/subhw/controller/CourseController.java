package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.dto.request.CourseCreateDTO;
import kr.ac.kopo.lyh.subhw.dto.response.CourseResponseDTO;
import kr.ac.kopo.lyh.subhw.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses(Authentication authentication) {
        // 사용자 역할에 따라 다른 과목 목록 반환
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        List<CourseResponseDTO> courses;
        if ("ROLE_PROFESSOR".equals(role)) {
            courses = courseService.getCoursesByProfessor(authentication.getName());
        } else if ("ROLE_STUDENT".equals(role)) {
            courses = courseService.getCoursesByStudent(authentication.getName());
        } else {
            courses = courseService.getAllCourses();
        }

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseCreateDTO courseDTO,
                                         Authentication authentication) {
        try {
            CourseResponseDTO course = courseService.createCourse(courseDTO, authentication.getName());
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourse(@PathVariable Long id,
                                         @Valid @RequestBody CourseCreateDTO courseDTO) {
        try {
            CourseResponseDTO course = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/enroll")
    @ResponseBody
    public ResponseEntity<?> enrollInCourse(@PathVariable Long id, Authentication authentication) {
        try {
            courseService.enrollInCourse(id, authentication.getName());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}