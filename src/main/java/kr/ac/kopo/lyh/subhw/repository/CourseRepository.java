package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Course;
import kr.ac.kopo.lyh.subhw.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByProfessor(Professor professor);

    List<Course> findBySemesterAndYear(String semester, Integer year);

    @Query("SELECT c FROM Course c WHERE c.professor.id = :professorId")
    List<Course> findByProfessorId(Long professorId);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(Long studentId);

    @Query("SELECT c FROM Course c JOIN FETCH c.assignments WHERE c.id = :id")
    Optional<Course> findByIdWithAssignments(Long id);

    @Query("SELECT c FROM Course c JOIN FETCH c.students WHERE c.id = :id")
    Optional<Course> findByIdWithStudents(Long id);

    boolean existsByCourseCode(String courseCode);
}