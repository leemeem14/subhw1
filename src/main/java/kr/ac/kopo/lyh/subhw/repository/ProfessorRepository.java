package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByUserId(Long userId);

    @Query("SELECT p FROM Professor p WHERE p.user.username = :username")
    Optional<Professor> findByUsername(String username);

    List<Professor> findByDepartment(String department);

    @Query("SELECT p FROM Professor p JOIN FETCH p.courses WHERE p.id = :id")
    Optional<Professor> findByIdWithCourses(Long id);
}