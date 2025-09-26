package kr.ac.kopo.lyh.subhw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e, Model model) {
        log.error("Runtime Exception 발생: ", e);
        model.addAttribute("error", "시스템 오류가 발생했습니다: " + e.getMessage());
        return "error/500";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e,
                                              RedirectAttributes redirectAttributes) {
        log.error("파일 크기 초과: ", e);
        redirectAttributes.addFlashAttribute("error", "파일 크기가 너무 큽니다. 10MB 이하의 파일을 업로드해주세요.");
        return "redirect:/posts";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.error("접근 권한 없음: ", e);
        model.addAttribute("error", "접근 권한이 없습니다.");
        return "error/403";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        log.error("잘못된 요청: ", e);
        model.addAttribute("error", "잘못된 요청입니다: " + e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception e, Model model) {
        log.error("예상치 못한 오류 발생: ", e);
        model.addAttribute("error", "예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return "error/500";
    }
}