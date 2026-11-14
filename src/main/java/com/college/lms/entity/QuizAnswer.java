package com.college.lms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(length = 200)
    private String selectedOptionIds; // Comma-separated option IDs for MCQ/Multiple select

    @Column(columnDefinition = "TEXT")
    private String textAnswer; // For Short Answer / Subjective

    private Double marksAwarded = 0.0;
    private boolean evaluated = false;

    @Column(columnDefinition = "TEXT")
    private String comments;

    public QuizAnswer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuizAttempt getAttempt() { return attempt; }
    public void setAttempt(QuizAttempt attempt) { this.attempt = attempt; }

    public QuizQuestion getQuestion() { return question; }
    public void setQuestion(QuizQuestion question) { this.question = question; }

    public String getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(String selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }

    public String getTextAnswer() { return textAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }

    public Double getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(Double marksAwarded) { this.marksAwarded = marksAwarded; }

    public boolean isEvaluated() { return evaluated; }
    public void setEvaluated(boolean evaluated) { this.evaluated = evaluated; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
