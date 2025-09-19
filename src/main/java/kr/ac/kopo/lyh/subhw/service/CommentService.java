package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.dto.CommentCreateDto;
import kr.ac.kopo.lyh.subhw.entity.Comment;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(CommentCreateDto dto, Post post, User author) {
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .post(post)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    public Comment updateComment(Long id, CommentCreateDto dto, User currentUser) {
        Comment comment = getCommentById(id);

        if (!comment.getAuthor().equals(currentUser)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, User currentUser) {
        Comment comment = getCommentById(id);

        if (!comment.getAuthor().equals(currentUser)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}