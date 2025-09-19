package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.dto.PostCreateDto;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    public Post createPost(PostCreateDto dto, User author) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .type(dto.getType())
                .dueDate(dto.getDueDate())
                .build();

        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public Post updatePost(Long id, PostCreateDto dto, User currentUser) {
        Post post = getPostById(id);

        if (!post.getAuthor().equals(currentUser)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setType(dto.getType());
        post.setDueDate(dto.getDueDate());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void deletePost(Long id, User currentUser) {
        Post post = getPostById(id);

        if (!post.getAuthor().equals(currentUser)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByAuthor(User author, Pageable pageable) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(
                keyword, keyword, pageable);
    }
}