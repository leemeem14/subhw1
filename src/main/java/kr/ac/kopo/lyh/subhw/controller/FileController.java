package kr.ac.kopo.lyh.subhw.controller;

import kr.ac.kopo.lyh.subhw.entity.AssignmentFile;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            AssignmentFile file = fileService.getFileById(id);
            byte[] fileContent = fileService.downloadFile(id);

            String encodedFileName = URLEncoder.encode(file.getOriginalFileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getOriginalFileName() + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteFile(@PathVariable Long id,
                             @RequestParam Long postId,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {

        try {
            fileService.deleteFile(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "파일이 삭제되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/posts/" + postId;
    }
}
