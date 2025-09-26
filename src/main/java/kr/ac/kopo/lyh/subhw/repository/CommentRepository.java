package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Comment;
import kr.ac.kopo.lyh.subhw.entity.Submission;
import kr.ac.kopo.lyh.subhw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findBySubmission(Submission submission);

    List<Comment> findBySubmissionId(Long submissionId);

    List<Comment> findByAuthor(User author);

    List<Comment> findByIsFeedbackTrue();

    List<Comment> findByIsFeedbackFalse();

    @Query("SELECT c FROM Comment c WHERE c.submission.id = :submissionId ORDER BY c.createdAt ASC")
    List<Comment> findBySubmissionIdOrderByCreatedAt(Long submissionId);

    @Query("SELECT c FROM Comment c WHERE c.submission.id = :submissionId AND c.isFeedback = :isFeedback ORDER BY c.createdAt ASC")
    List<Comment> findBySubmissionIdAndIsFeedback(Long submissionId, Boolean isFeedback);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId ORDER BY c.createdAt DESC")
    List<Comment> findByAuthorIdOrderByCreatedAt(Long authorId);
}