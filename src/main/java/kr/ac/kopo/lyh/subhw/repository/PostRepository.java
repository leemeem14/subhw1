package kr.ac.kopo.lyh.subhw.repository;

import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(
            String title, String content, Pageable pageable);
}