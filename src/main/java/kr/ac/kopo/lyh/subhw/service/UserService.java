package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.dto.UserRegistrationDto;
import kr.ac.kopo.lyh.entity.User;
import kr.ac.kopo.lyh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String PROFILE_IMAGE_DIR = "uploads/profiles/";

    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .realName(dto.getRealName())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(Long userId, String realName, String phoneNumber, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setRealName(realName);
        user.setPhoneNumber(phoneNumber);

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImagePath = saveProfileImage(profileImage);
            user.setProfileImage(profileImagePath);
        }

        return userRepository.save(user);
    }

    private String saveProfileImage(MultipartFile file) {
        try {
            // 디렉토리 생성
            Path uploadPath = Paths.get(PROFILE_IMAGE_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath);

            return PROFILE_IMAGE_DIR + fileName;
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
        }
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
