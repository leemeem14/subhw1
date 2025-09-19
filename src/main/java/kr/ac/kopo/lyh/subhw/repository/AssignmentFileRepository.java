package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.AssignmentFile;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Long> {
    List<AssignmentFile> findByPostOrderByUploadedAtDesc(Post post);
    List<AssignmentFile> findByPostAndUploaderOrderByUploadedAtDesc(Post post, User uploader);
    List<AssignmentFile> findByUploaderOrderByUploadedAtDesc(User uploader);
}
