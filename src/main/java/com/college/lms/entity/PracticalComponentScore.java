package com.college.lms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "practical_component_scores")
public class PracticalComponentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private PracticalSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private PracticalRubricComponent component;

    private Double marksObtained = 0.0;

    @Column(length = 255)
    private String remarks;

    public PracticalComponentScore() {}

    public PracticalComponentScore(PracticalSubmission submission, PracticalRubricComponent component, Double marksObtained, String remarks) {
        this.submission = submission;
        this.component = component;
        this.marksObtained = marksObtained;
        this.remarks = remarks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PracticalSubmission getSubmission() { return submission; }
    public void setSubmission(PracticalSubmission submission) { this.submission = submission; }

    public PracticalRubricComponent getComponent() { return component; }
    public void setComponent(PracticalRubricComponent component) { this.component = component; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
