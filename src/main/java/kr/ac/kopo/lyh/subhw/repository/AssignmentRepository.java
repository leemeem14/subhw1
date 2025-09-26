package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Assignment;
import kr.ac.kopo.lyh.subhw.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourse(Course course);

    List<Assignment> findByCourseId(Long courseId);

    List<Assignment> findByDueDateBefore(LocalDateTime dateTime);

    List<Assignment> findByDueDateAfter(LocalDateTime dateTime);

    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId ORDER BY a.dueDate ASC")
    List<Assignment> findByCourseIdOrderByDueDate(Long courseId);

    @Query("SELECT a FROM Assignment a JOIN FETCH a.submissions WHERE a.id = :id")
    Optional<Assignment> findByIdWithSubmissions(Long id);

    @Query("SELECT a FROM Assignment a WHERE a.course.professor.id = :professorId")
    List<Assignment> findByProfessorId(Long professorId);
}