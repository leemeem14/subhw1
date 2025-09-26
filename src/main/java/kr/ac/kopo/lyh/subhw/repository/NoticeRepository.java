package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Notice;
import kr.ac.kopo.lyh.subhw.entity.Course;
import kr.ac.kopo.lyh.subhw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCourse(Course course);

    List<Notice> findByCourseId(Long courseId);

    List<Notice> findByAuthor(User author);

    // 전체 공지사항 (course가 null인 경우)
    List<Notice> findByCourseIsNull();

    // 중요 공지사항
    List<Notice> findByIsImportantTrue();

    @Query("SELECT n FROM Notice n WHERE n.course IS NULL OR n.course.id IN :courseIds ORDER BY n.isImportant DESC, n.createdAt DESC")
    List<Notice> findRelevantNotices(List<Long> courseIds);

    @Query("SELECT n FROM Notice n ORDER BY n.isImportant DESC, n.createdAt DESC")
    List<Notice> findAllOrderByImportantAndCreatedAt();

    @Query("SELECT n FROM Notice n WHERE n.course.id = :courseId ORDER BY n.isImportant DESC, n.createdAt DESC")
    List<Notice> findByCourseIdOrderByImportantAndCreatedAt(Long courseId);
}