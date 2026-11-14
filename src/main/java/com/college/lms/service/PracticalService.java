package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PracticalService {

    private final PracticalRepository practicalRepository;
    private final PracticalRubricComponentRepository rubricRepository;
    private final PracticalSubmissionRepository submissionRepository;
    private final PracticalComponentScoreRepository scoreRepository;
    private final NotificationService notificationService;

    public PracticalService(PracticalRepository practicalRepository,
                            PracticalRubricComponentRepository rubricRepository,
                            PracticalSubmissionRepository submissionRepository,
                            PracticalComponentScoreRepository scoreRepository,
                            NotificationService notificationService) {
        this.practicalRepository = practicalRepository;
        this.rubricRepository = rubricRepository;
        this.submissionRepository = submissionRepository;
        this.scoreRepository = scoreRepository;
        this.notificationService = notificationService;
    }

    public List<Practical> getPracticalsByTeacher(User teacher) {
        return practicalRepository.findByTeacherOrderByCreatedAtDesc(teacher);
    }

    public List<Practical> getPracticalsBySubject(Subject subject) {
        return practicalRepository.findBySubjectOrderByExperimentNumberAsc(subject);
    }

    public List<Practical> getPracticalsForSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) return List.of();
        return practicalRepository.findBySubjectInOrderByDueDateAsc(subjects);
    }

    public Optional<Practical> getPracticalById(Long id) {
        return practicalRepository.findById(id);
    }

    @Transactional
    public Practical savePractical(Practical practical, List<PracticalRubricComponent> rubricComponents) {
        boolean isNew = (practical.getId() == null);
        Practical saved = practicalRepository.save(practical);

        if (rubricComponents != null && !rubricComponents.isEmpty()) {
            int order = 1;
            for (PracticalRubricComponent comp : rubricComponents) {
                comp.setPractical(saved);
                comp.setOrderIndex(order++);
                rubricRepository.save(comp);
            }
        }

        if (isNew && saved.getSubject() != null) {
            for (Enrollment e : saved.getSubject().getEnrollments()) {
                notificationService.createNotification(
                    e.getStudent().getUser(),
                    "New Practical: " + saved.getExperimentNumber() + " - " + saved.getTitle(),
                    "Practical experiment assigned in " + saved.getSubject().getName() + ". Total Marks: " + saved.getMaxMarks(),
                    "/student/practicals/" + saved.getId(),
                    "PRACTICAL"
                );
            }
        }

        return saved;
    }

    @Transactional
    public void deletePractical(Long id) {
        practicalRepository.deleteById(id);
    }

    public Optional<PracticalSubmission> getSubmission(Practical practical, StudentProfile student) {
        return submissionRepository.findByPracticalAndStudent(practical, student);
    }

    public Optional<PracticalSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    public List<PracticalSubmission> getSubmissionsByPractical(Practical practical) {
        return submissionRepository.findByPractical(practical);
    }

    public List<PracticalSubmission> getSubmissionsByStudent(StudentProfile student) {
        return submissionRepository.findByStudentOrderBySubmissionDateDesc(student);
    }

    @Transactional
    public PracticalSubmission submitPractical(Practical practical, StudentProfile student,
                                               String reportFileUrl, String reportFileName, String remarks) {
        PracticalSubmission submission = submissionRepository.findByPracticalAndStudent(practical, student)
                .orElseGet(() -> {
                    PracticalSubmission s = new PracticalSubmission();
                    s.setPractical(practical);
                    s.setStudent(student);
                    return s;
                });

        submission.setSubmissionDate(LocalDateTime.now());
        if (reportFileUrl != null && !reportFileUrl.isEmpty()) {
            submission.setReportFileUrl(reportFileUrl);
            submission.setReportFileName(reportFileName);
        }
        submission.setRemarks(remarks);
        submission.setStatus("SUBMITTED");

        PracticalSubmission saved = submissionRepository.save(submission);

        // Notify teacher
        notificationService.createNotification(
            practical.getTeacher(),
            "Lab Submission: " + student.getUser().getFullName(),
            student.getUser().getFullName() + " submitted lab report for " + practical.getExperimentNumber(),
            "/teacher/practicals/" + practical.getId() + "/submissions",
            "SUBMISSION"
        );

        return saved;
    }

    @Transactional
    public PracticalSubmission evaluatePractical(Long submissionId, Map<Long, Double> componentScoresMap,
                                                 String feedback, User teacher) {
        PracticalSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        double total = 0.0;
        List<PracticalRubricComponent> components = submission.getPractical().getRubricComponents();

        for (PracticalRubricComponent comp : components) {
            Double score = componentScoresMap.getOrDefault(comp.getId(), 0.0);
            if (score > comp.getMaxMarks()) {
                score = (double) comp.getMaxMarks();
            }

            Optional<PracticalComponentScore> existingScore = scoreRepository.findBySubmissionAndComponent(submission, comp);
            PracticalComponentScore compScore = existingScore.orElseGet(() -> new PracticalComponentScore(submission, comp, 0.0, ""));
            compScore.setMarksObtained(score);
            scoreRepository.save(compScore);

            total += score;
        }

        submission.setTotalMarksObtained(total);
        submission.setStatus("EVALUATED");
        submission.setEvaluatedAt(LocalDateTime.now());
        submission.setTeacherFeedback(feedback);

        PracticalSubmission saved = submissionRepository.save(submission);

        // Notify student
        notificationService.createNotification(
            submission.getStudent().getUser(),
            "Practical Evaluated: " + submission.getPractical().getExperimentNumber(),
            "Your lab record has been evaluated. Total Marks: " + total + " / " + submission.getPractical().getMaxMarks(),
            "/student/practicals/" + submission.getPractical().getId(),
            "MARKS"
        );

        return saved;
    }
}
