package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.AssessmentRepository;
import com.college.lms.repository.MarksAuditLogRepository;
import com.college.lms.repository.StudentMarkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MarksService {

    private final AssessmentRepository assessmentRepository;
    private final StudentMarkRepository studentMarkRepository;
    private final MarksAuditLogRepository auditLogRepository;
    private final NotificationService notificationService;

    public MarksService(AssessmentRepository assessmentRepository,
                        StudentMarkRepository studentMarkRepository,
                        MarksAuditLogRepository auditLogRepository,
                        NotificationService notificationService) {
        this.assessmentRepository = assessmentRepository;
        this.studentMarkRepository = studentMarkRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationService = notificationService;
    }

    public List<Assessment> getAssessmentsBySubject(Subject subject) {
        return assessmentRepository.findBySubjectOrderByAssessmentDateDesc(subject);
    }

    public List<Assessment> getAssessmentsByTeacher(User teacher) {
        return assessmentRepository.findByTeacherOrderByAssessmentDateDesc(teacher);
    }

    public List<Assessment> getAssessmentsForSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) return List.of();
        return assessmentRepository.findBySubjectInOrderByAssessmentDateDesc(subjects);
    }

    public Optional<Assessment> getAssessmentById(Long id) {
        return assessmentRepository.findById(id);
    }

    @Transactional
    public Assessment saveAssessment(Assessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public List<StudentMark> getMarksByAssessment(Assessment assessment) {
        return studentMarkRepository.findByAssessment(assessment);
    }

    public List<StudentMark> getMarksByStudent(StudentProfile student) {
        return studentMarkRepository.findByStudent(student);
    }

    public List<StudentMark> getMarksByStudentAndSubject(StudentProfile student, Subject subject) {
        return studentMarkRepository.findByStudentAndSubject(student, subject);
    }

    public List<StudentMark> getRecentMarksByStudent(StudentProfile student) {
        return studentMarkRepository.findRecentMarksByStudent(student);
    }

    @Transactional
    public StudentMark recordMark(Assessment assessment, StudentProfile student,
                                  double marksObtained, String remarks, String reason, User teacher) {
        if (marksObtained < 0 || marksObtained > assessment.getMaxMarks()) {
            throw new IllegalArgumentException("Marks must be between 0 and " + assessment.getMaxMarks());
        }

        Optional<StudentMark> existingOpt = studentMarkRepository.findByAssessmentAndStudent(assessment, student);
        StudentMark mark;
        double previousMarks = 0.0;

        if (existingOpt.isPresent()) {
            mark = existingOpt.get();
            previousMarks = mark.getMarksObtained();
        } else {
            mark = new StudentMark();
            mark.setAssessment(assessment);
            mark.setStudent(student);
        }

        mark.setMarksObtained(marksObtained);
        mark.setTeacherRemarks(remarks);
        mark.setLastModifiedBy(teacher);
        mark.setLastModifiedAt(LocalDateTime.now());

        StudentMark saved = studentMarkRepository.save(mark);

        // Record Audit Log if marks changed
        if (existingOpt.isPresent() && previousMarks != marksObtained) {
            MarksAuditLog auditLog = new MarksAuditLog(
                saved,
                previousMarks,
                marksObtained,
                teacher,
                reason != null && !reason.isEmpty() ? reason : "Marks updated by instructor"
            );
            auditLogRepository.save(auditLog);
        }

        // Notify student of marks
        notificationService.createNotification(
            student.getUser(),
            "Marks Published: " + assessment.getTitle(),
            "Your marks for " + assessment.getTitle() + " in " + assessment.getSubject().getName() + " have been published. Score: " + marksObtained + " / " + assessment.getMaxMarks(),
            "/student/results",
            "MARKS"
        );

        return saved;
    }

    public Map<String, Object> calculateStudentPerformance(StudentProfile student) {
        List<StudentMark> marksList = studentMarkRepository.findByStudent(student);
        double totalObtained = 0.0;
        double totalMax = 0.0;

        Map<String, List<StudentMark>> subjectMarksMap = new LinkedHashMap<>();
        Map<String, Double> subjectPercentages = new LinkedHashMap<>();

        for (StudentMark mark : marksList) {
            String subjectName = mark.getAssessment().getSubject().getName();
            subjectMarksMap.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(mark);
            totalObtained += mark.getMarksObtained();
            totalMax += mark.getAssessment().getMaxMarks();
        }

        for (Map.Entry<String, List<StudentMark>> entry : subjectMarksMap.entrySet()) {
            double sObtained = 0;
            double sMax = 0;
            for (StudentMark sm : entry.getValue()) {
                sObtained += sm.getMarksObtained();
                sMax += sm.getAssessment().getMaxMarks();
            }
            double sPct = (sMax > 0) ? (sObtained / sMax) * 100.0 : 0.0;
            subjectPercentages.put(entry.getKey(), Math.round(sPct * 10.0) / 10.0);
        }

        double overallPercentage = (totalMax > 0) ? (totalObtained / totalMax) * 100.0 : 0.0;
        String grade = calculateGrade(overallPercentage);

        Map<String, Object> performance = new HashMap<>();
        performance.put("totalObtained", Math.round(totalObtained * 10.0) / 10.0);
        performance.put("totalMax", Math.round(totalMax * 10.0) / 10.0);
        performance.put("overallPercentage", Math.round(overallPercentage * 10.0) / 10.0);
        performance.put("grade", grade);
        performance.put("subjectPercentages", subjectPercentages);
        performance.put("subjectMarksMap", subjectMarksMap);
        performance.put("allMarks", marksList);

        return performance;
    }

    public static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        if (percentage >= 40) return "E";
        return "F";
    }
}
