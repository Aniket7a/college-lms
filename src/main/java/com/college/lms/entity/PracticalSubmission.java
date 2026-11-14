package com.college.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "practical_submissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"practical_id", "student_id"})
})
public class PracticalSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practical_id", nullable = false)
    private Practical practical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    private LocalDateTime submissionDate = LocalDateTime.now();

    private String reportFileUrl;
    private String reportFileName;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private Double totalMarksObtained;

    // Status: SUBMITTED, UNDER_EVALUATION, EVALUATED
    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED";

    private LocalDateTime evaluatedAt;

    @Column(columnDefinition = "TEXT")
    private String teacherFeedback;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PracticalComponentScore> componentScores = new ArrayList<>();

    public PracticalSubmission() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Practical getPractical() { return practical; }
    public void setPractical(Practical practical) { this.practical = practical; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public String getReportFileUrl() { return reportFileUrl; }
    public void setReportFileUrl(String reportFileUrl) { this.reportFileUrl = reportFileUrl; }

    public String getReportFileName() { return reportFileName; }
    public void setReportFileName(String reportFileName) { this.reportFileName = reportFileName; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Double getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }

    public String getTeacherFeedback() { return teacherFeedback; }
    public void setTeacherFeedback(String teacherFeedback) { this.teacherFeedback = teacherFeedback; }

    public List<PracticalComponentScore> getComponentScores() { return componentScores; }
    public void setComponentScores(List<PracticalComponentScore> componentScores) { this.componentScores = componentScores; }
}
