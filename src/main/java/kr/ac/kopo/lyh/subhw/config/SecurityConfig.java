package kr.ac.kopo.lyh.subhw.config;

import com.assignment.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 정적 리소스 및 공개 페이지
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/home", "/login", "/register").permitAll()

                // API 엔드포인트 권한 설정
                .requestMatchers("/api/auth/**").permitAll()

                // 교수 전용 API
                .requestMatchers("/api/courses/create", "/api/courses/*/edit", "/api/courses/*/delete").hasRole("PROFESSOR")
                .requestMatchers("/api/assignments/create", "/api/assignments/*/edit", "/api/assignments/*/delete").hasRole("PROFESSOR")
                .requestMatchers("/api/submissions/*/grade").hasRole("PROFESSOR")
                .requestMatchers("/api/notices/create", "/api/notices/*/edit", "/api/notices/*/delete").hasRole("PROFESSOR")

                // 학생 전용 API
                .requestMatchers("/api/assignments/*/submit").hasRole("STUDENT")
                .requestMatchers("/api/courses/*/enroll", "/api/courses/*/unenroll").hasRole("STUDENT")

                // 인증된 사용자만 접근 가능한 API
                .requestMatchers("/api/**").authenticated()

                // 웹 페이지 권한 설정
                .requestMatchers("/professor/**").hasRole("PROFESSOR")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/dashboard/**").authenticated()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}