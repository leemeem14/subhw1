package kr.ac.kopo.lyh.subhw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "student_number", unique = true, nullable = false, length = 20)
    private String studentNumber;

    @Column(nullable = false, length = 100)
    private String major;

    @Column
    private Integer grade;

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    private List<Course> courses;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Submission> submissions;
}