package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Submission;
import kr.ac.kopo.lyh.subhw.entity.Assignment;
import kr.ac.kopo.lyh.subhw.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignment(Assignment assignment);

    List<Submission> findByStudent(Student student);

    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, Student student);

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId")
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("SELECT s FROM Submission s JOIN FETCH s.comments WHERE s.id = :id")
    Optional<Submission> findByIdWithComments(Long id);

    List<Submission> findByStatus(Submission.Status status);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.professor.id = :professorId")
    List<Submission> findByProfessorId(Long professorId);

    boolean existsByAssignmentAndStudent(Assignment assignment, Student student);
}