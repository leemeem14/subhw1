package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.entity.AssignmentFile;
import kr.ac.kopo.lyh.subhw.entity.FileType;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.repository.AssignmentFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final AssignmentFileRepository fileRepository;

    private static final String UPLOAD_DIR = "uploads/assignments/";

    public AssignmentFile saveFile(MultipartFile file, Post post, User uploader, FileType fileType) {
        try {
            // 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유한 파일명 생성
            String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(storedFileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath);

            // 파일 정보를 데이터베이스에 저장
            AssignmentFile assignmentFile = AssignmentFile.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .post(post)
                    .uploader(uploader)
                    .fileType(fileType)
                    .build();

            return fileRepository.save(assignmentFile);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public AssignmentFile getFileById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<AssignmentFile> getFilesByPost(Post post) {
        return fileRepository.findByPostOrderByUploadedAtDesc(post);
    }

    @Transactional(readOnly = true)
    public List<AssignmentFile> getFilesByPostAndUploader(Post post, User uploader) {
        return fileRepository.findByPostAndUploaderOrderByUploadedAtDesc(post, uploader);
    }

    public void deleteFile(Long fileId, User currentUser) {
        AssignmentFile file = getFileById(fileId);

        if (!file.getUploader().equals(currentUser)) {
            throw new RuntimeException("파일을 삭제할 권한이 없습니다.");
        }

        try {
            // 실제 파일 삭제
            Path filePath = Paths.get(file.getFilePath());
            Files.deleteIfExists(filePath);

            // 데이터베이스에서 삭제
            fileRepository.delete(file);

        } catch (IOException e) {
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    public byte[] downloadFile(Long fileId) {
        AssignmentFile file = getFileById(fileId);

        try {
            Path filePath = Paths.get(file.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드에 실패했습니다.", e);
        }
    }
}