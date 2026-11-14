package com.college.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssessmentType assessmentType;

    private double maxMarks = 50.0;
    private LocalDate assessmentDate = LocalDate.now();

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentMark> studentMarks = new ArrayList<>();

    public Assessment() {}

    public Assessment(Subject subject, User teacher, String title, AssessmentType assessmentType, double maxMarks, LocalDate assessmentDate) {
        this.subject = subject;
        this.teacher = teacher;
        this.title = title;
        this.assessmentType = assessmentType;
        this.maxMarks = maxMarks;
        this.assessmentDate = assessmentDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public AssessmentType getAssessmentType() { return assessmentType; }
    public void setAssessmentType(AssessmentType assessmentType) { this.assessmentType = assessmentType; }

    public double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<StudentMark> getStudentMarks() { return studentMarks; }
    public void setStudentMarks(List<StudentMark> studentMarks) { this.studentMarks = studentMarks; }
}
