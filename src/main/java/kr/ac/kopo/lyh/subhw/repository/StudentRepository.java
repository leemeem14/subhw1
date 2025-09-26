package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByStudentNumber(String studentNumber);

    @Query("SELECT s FROM Student s WHERE s.user.username = :username")
    Optional<Student> findByUsername(String username);

    List<Student> findByMajor(String major);

    List<Student> findByGrade(Integer grade);

    @Query("SELECT s FROM Student s JOIN FETCH s.courses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(Long id);

    boolean existsByStudentNumber(String studentNumber);
}