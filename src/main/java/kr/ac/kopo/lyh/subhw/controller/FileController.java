package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.entity.AssignmentFile;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        try {
            AssignmentFile file = fileService.getFileById(fileId);
            byte[] data = fileService.downloadFile(fileId);

            ByteArrayResource resource = new ByteArrayResource(data);

            String encodedFilename = URLEncoder.encode(file.getOriginalFileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable Long fileId,
                           @AuthenticationPrincipal User currentUser,
                           RedirectAttributes redirectAttributes) {
        try {
            AssignmentFile file = fileService.getFileById(fileId);
            Long postId = file.getPost().getId();

            fileService.deleteFile(fileId, currentUser);

            redirectAttributes.addFlashAttribute("success", "파일이 삭제되었습니다.");
            return "redirect:/posts/" + postId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/posts";
        }
    }
}