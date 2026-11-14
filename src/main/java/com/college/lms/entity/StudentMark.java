package com.college.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_marks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assessment_id", "student_id"})
})
public class StudentMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    private double marksObtained = 0.0;

    @Column(length = 255)
    private String teacherRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by")
    private User lastModifiedBy;

    private LocalDateTime lastModifiedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "studentMark", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedAt DESC")
    private List<MarksAuditLog> auditLogs = new ArrayList<>();

    public StudentMark() {}

    public StudentMark(Assessment assessment, StudentProfile student, double marksObtained, String teacherRemarks, User lastModifiedBy) {
        this.assessment = assessment;
        this.student = student;
        this.marksObtained = marksObtained;
        this.teacherRemarks = teacherRemarks;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public String getTeacherRemarks() { return teacherRemarks; }
    public void setTeacherRemarks(String teacherRemarks) { this.teacherRemarks = teacherRemarks; }

    public User getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(User lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public List<MarksAuditLog> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<MarksAuditLog> auditLogs) { this.auditLogs = auditLogs; }

    public double getPercentage() {
        if (assessment == null || assessment.getMaxMarks() <= 0) return 0.0;
        return (marksObtained / assessment.getMaxMarks()) * 100.0;
    }
}
