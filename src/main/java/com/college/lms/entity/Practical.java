package com.college.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "practicals")
public class Practical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false, length = 50)
    private String experimentNumber; // e.g. "EXP-01"

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String labManualUrl;
    private String labManualName;

    @Column(columnDefinition = "TEXT")
    private String vivaQuestions;

    private int maxMarks = 30;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "practical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PracticalRubricComponent> rubricComponents = new ArrayList<>();

    @OneToMany(mappedBy = "practical", cascade = CascadeType.ALL)
    private List<PracticalSubmission> submissions = new ArrayList<>();

    public Practical() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }

    public String getExperimentNumber() { return experimentNumber; }
    public void setExperimentNumber(String experimentNumber) { this.experimentNumber = experimentNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLabManualUrl() { return labManualUrl; }
    public void setLabManualUrl(String labManualUrl) { this.labManualUrl = labManualUrl; }

    public String getLabManualName() { return labManualName; }
    public void setLabManualName(String labManualName) { this.labManualName = labManualName; }

    public String getVivaQuestions() { return vivaQuestions; }
    public void setVivaQuestions(String vivaQuestions) { this.vivaQuestions = vivaQuestions; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<PracticalRubricComponent> getRubricComponents() { return rubricComponents; }
    public void setRubricComponents(List<PracticalRubricComponent> rubricComponents) { this.rubricComponents = rubricComponents; }

    public List<PracticalSubmission> getSubmissions() { return submissions; }
    public void setSubmissions(List<PracticalSubmission> submissions) { this.submissions = submissions; }
}
