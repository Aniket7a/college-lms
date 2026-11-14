package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.AssignmentRepository;
import com.college.lms.repository.AssignmentSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final NotificationService notificationService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository submissionRepository,
                             NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.notificationService = notificationService;
    }

    public List<Assignment> getAssignmentsBySubject(Subject subject) {
        return assignmentRepository.findBySubjectOrderByDueDateAsc(subject);
    }

    public List<Assignment> getAssignmentsByTeacher(User teacher) {
        return assignmentRepository.findByTeacherOrderByDueDateDesc(teacher);
    }

    public List<Assignment> getAssignmentsForSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) return List.of();
        return assignmentRepository.findBySubjectInOrderByDueDateDesc(subjects);
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Transactional
    public Assignment saveAssignment(Assignment assignment) {
        boolean isNew = (assignment.getId() == null);
        Assignment saved = assignmentRepository.save(assignment);

        if (isNew && saved.getSubject() != null) {
            // Send notification to enrolled students
            for (Enrollment e : saved.getSubject().getEnrollments()) {
                notificationService.createNotification(
                    e.getStudent().getUser(),
                    "New Assignment: " + saved.getTitle(),
                    "A new assignment has been posted in " + saved.getSubject().getName() + ". Due date: " + saved.getDueDate(),
                    "/student/assignments/" + saved.getId(),
                    "ASSIGNMENT"
                );
            }
        }

        return saved;
    }

    @Transactional
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    public List<AssignmentSubmission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public Optional<AssignmentSubmission> getSubmission(Assignment assignment, StudentProfile student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    public Optional<AssignmentSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    public List<AssignmentSubmission> getSubmissionsByStudent(StudentProfile student) {
        return submissionRepository.findByStudentOrderBySubmissionDateDesc(student);
    }

    @Transactional
    public AssignmentSubmission submitAssignment(Assignment assignment, StudentProfile student,
                                                 String textContent, String fileUrl, String fileName) {
        LocalDateTime now = LocalDateTime.now();

        // Check if existing submission
        Optional<AssignmentSubmission> existingOpt = submissionRepository.findByAssignmentAndStudent(assignment, student);
        AssignmentSubmission submission;

        if (existingOpt.isPresent()) {
            submission = existingOpt.get();
            // Check if deadline passed and resubmission not allowed
            if (assignment.isExpired() && !submission.isResubmissionAllowed()) {
                throw new IllegalStateException("Submission deadline has passed and resubmission is not enabled.");
            }
        } else {
            submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
        }

        submission.setSubmissionDate(now);
        submission.setTextContent(textContent);
        if (fileUrl != null && !fileUrl.isEmpty()) {
            submission.setFileUrl(fileUrl);
            submission.setFileName(fileName);
        }

        // Determine status
        if (assignment.getDueDate() != null && now.isAfter(assignment.getDueDate())) {
            submission.setStatus("LATE");
        } else {
            submission.setStatus("SUBMITTED");
        }

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Notify teacher
        notificationService.createNotification(
            assignment.getTeacher(),
            "Assignment Submitted: " + student.getUser().getFullName(),
            student.getUser().getFullName() + " (" + student.getRollNumber() + ") submitted assignment '" + assignment.getTitle() + "'.",
            "/teacher/assignments/" + assignment.getId() + "/submissions",
            "SUBMISSION"
        );

        return saved;
    }

    @Transactional
    public AssignmentSubmission evaluateSubmission(Long submissionId, Double marks, String feedback, User teacher) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (marks > submission.getAssignment().getMaxMarks()) {
            throw new IllegalArgumentException("Marks cannot exceed maximum marks (" + submission.getAssignment().getMaxMarks() + ")");
        }

        submission.setMarksObtained(marks);
        submission.setFeedback(feedback);
        submission.setEvaluatedBy(teacher);
        submission.setEvaluatedAt(LocalDateTime.now());
        submission.setStatus("EVALUATED");

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Notify student
        notificationService.createNotification(
            submission.getStudent().getUser(),
            "Assignment Evaluated: " + submission.getAssignment().getTitle(),
            "Your assignment has been graded. Marks: " + marks + " / " + submission.getAssignment().getMaxMarks() + ". Feedback: " + (feedback != null ? feedback : "None"),
            "/student/assignments/" + submission.getAssignment().getId(),
            "MARKS"
        );

        return saved;
    }

    @Transactional
    public void toggleResubmission(Long submissionId) {
        submissionRepository.findById(submissionId).ifPresent(s -> {
            s.setResubmissionAllowed(!s.isResubmissionAllowed());
            submissionRepository.save(s);
        });
    }

    public long countActiveAssignments() {
        return assignmentRepository.countActiveAssignments(LocalDateTime.now());
    }

    public long countActiveAssignmentsByTeacher(User teacher) {
        return assignmentRepository.countActiveAssignmentsByTeacher(teacher, LocalDateTime.now());
    }

    public long countPendingEvaluationsByTeacher(User teacher) {
        return submissionRepository.countPendingEvaluationsByTeacher(teacher);
    }

    public List<AssignmentSubmission> getRecentSubmissionsByTeacher(User teacher) {
        return submissionRepository.findRecentSubmissionsByTeacher(teacher);
    }

    public long getTotalSubmissions() {
        return submissionRepository.count();
    }
}
