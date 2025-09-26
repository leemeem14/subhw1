package kr.ac.kopo.lyh.subhw.service;

import kr.ac.kopo.lyh.subhw.dto.request.UserRegisterDTO;
import kr.ac.kopo.lyh.subhw.dto.response.UserResponseDTO;
import kr.ac.kopo.lyh.subhw.entity.Professor;
import kr.ac.kopo.lyh.subhw.entity.Student;
import kr.ac.kopo.lyh.subhw.entity.User;
import kr.ac.kopo.lyh.subhw.repository.ProfessorRepository;
import kr.ac.kopo.lyh.subhw.repository.StudentRepository;
import kr.ac.kopo.lyh.subhw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        // 중복 검사
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        if (registerDTO.getRole() == User.Role.STUDENT && 
            studentRepository.existsByStudentNumber(registerDTO.getStudentNumber())) {
            throw new RuntimeException("이미 존재하는 학번입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .role(registerDTO.getRole())
                .build();

        user = userRepository.save(user);

        // 역할별 추가 정보 저장
        if (registerDTO.getRole() == User.Role.PROFESSOR) {
            Professor professor = Professor.builder()
                    .user(user)
                    .department(registerDTO.getDepartment())
                    .position(registerDTO.getPosition())
                    .build();
            professorRepository.save(professor);
        } else if (registerDTO.getRole() == User.Role.STUDENT) {
            Student student = Student.builder()
                    .user(user)
                    .studentNumber(registerDTO.getStudentNumber())
                    .major(registerDTO.getMajor())
                    .grade(registerDTO.getGrade())
                    .build();
            studentRepository.save(student);
        }

        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByRole(User.Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Professor> findProfessorByUsername(String username) {
        return professorRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Student> findStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }
}