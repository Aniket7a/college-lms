package com.college.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks_audit_logs")
public class MarksAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_mark_id", nullable = false)
    private StudentMark studentMark;

    private double previousMarks;
    private double newMarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(length = 255)
    private String updateReason;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public MarksAuditLog() {}

    public MarksAuditLog(StudentMark studentMark, double previousMarks, double newMarks, User updatedBy, String updateReason) {
        this.studentMark = studentMark;
        this.previousMarks = previousMarks;
        this.newMarks = newMarks;
        this.updatedBy = updatedBy;
        this.updateReason = updateReason;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentMark getStudentMark() { return studentMark; }
    public void setStudentMark(StudentMark studentMark) { this.studentMark = studentMark; }

    public double getPreviousMarks() { return previousMarks; }
    public void setPreviousMarks(double previousMarks) { this.previousMarks = previousMarks; }

    public double getNewMarks() { return newMarks; }
    public void setNewMarks(double newMarks) { this.newMarks = newMarks; }

    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }

    public String getUpdateReason() { return updateReason; }
    public void setUpdateReason(String updateReason) { this.updateReason = updateReason; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
