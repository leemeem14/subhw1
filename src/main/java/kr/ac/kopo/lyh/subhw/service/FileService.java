package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.entity.AssignmentFile;
import kr.ac.kopo.lyh.subhw.entity.FileType;
import kr.ac.kopo.lyh.subhw.entity.Post;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.repository.AssignmentFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final AssignmentFileRepository fileRepository;

    @Value("${app.file.upload-dir:${user.home}/assignment-uploads}")
    private String uploadDir;

    public AssignmentFile saveFile(MultipartFile file, Post post, User uploader, FileType fileType) {
        validateFile(file);

        try {
            // 업로드 디렉토리 생성
            Path uploadPath = createUploadDirectory();

            // 안전한 파일명 생성
            String safeFileName = generateSafeFileName(file.getOriginalFilename());
            Path targetPath = uploadPath.resolve(safeFileName);

            // 파일 저장 (덮어쓰기 방지)
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("파일 저장 완료: {} -> {}", file.getOriginalFilename(), targetPath);

            // 데이터베이스에 파일 정보 저장
            AssignmentFile assignmentFile = AssignmentFile.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(safeFileName)
                    .filePath(targetPath.toString())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .post(post)
                    .uploader(uploader)
                    .fileType(fileType)
                    .build();

            return fileRepository.save(assignmentFile);

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("파일 크기가 10MB를 초과할 수 없습니다.");
        }
    }

    private Path createUploadDirectory() throws IOException {
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir, "assignments", dateFolder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("업로드 디렉토리 생성: {}", uploadPath);
        }

        return uploadPath;
    }

    private String generateSafeFileName(String originalFilename) {
        String cleanFileName = StringUtils.cleanPath(originalFilename);
        String extension = "";

        int dotIndex = cleanFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = cleanFileName.substring(dotIndex);
            cleanFileName = cleanFileName.substring(0, dotIndex);
        }

        // 특수문자 제거 및 UUID 추가
        String safeBaseName = cleanFileName.replaceAll("[^a-zA-Z0-9가-힣]", "_");
        return UUID.randomUUID().toString() + "_" + safeBaseName + extension;
    }

    @Transactional(readOnly = true)
    public AssignmentFile getFileById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다. ID: " + id));
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
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료: {}", filePath);
            }

            // 데이터베이스에서 삭제
            fileRepository.delete(file);

        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", file.getFilePath(), e);
            throw new RuntimeException("파일 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(Long fileId) {
        AssignmentFile file = getFileById(fileId);

        try {
            Path filePath = Paths.get(file.getFilePath());
            if (!Files.exists(filePath)) {
                throw new RuntimeException("파일이 존재하지 않습니다: " + file.getOriginalFileName());
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("파일 다운로드 실패: {}", file.getFilePath(), e);
            throw new RuntimeException("파일 다운로드에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
