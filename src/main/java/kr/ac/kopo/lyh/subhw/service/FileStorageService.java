package kr.ac.kopo.lyh.subhw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path fileStorageLocation;

    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (fileStorageLocation == null) {
            init();
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("잘못된 파일명입니다: " + fileName);
            }

            // 고유한 파일명 생성
            String fileExtension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = fileName.substring(dotIndex);
            }

            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("파일이 저장되었습니다: {}", uniqueFileName);
            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + fileName, e);
        }
    }

    public Path loadFileAsPath(String fileName) {
        if (fileStorageLocation == null) {
            init();
        }

        return fileStorageLocation.resolve(fileName).normalize();
    }

    public boolean deleteFile(String fileName) {
        try {
            Path filePath = loadFileAsPath(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("파일 삭제에 실패했습니다: {}", fileName, e);
            return false;
        }
    }
}