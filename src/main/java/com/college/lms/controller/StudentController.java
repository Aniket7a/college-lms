package com.college.lms.controller;

import com.college.lms.entity.*;
import com.college.lms.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final AcademicService academicService;
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final QuizService quizService;
    private final PracticalService practicalService;
    private final MarksService marksService;
    private final StudyMaterialService studyMaterialService;
    private final FileStorageService fileStorageService;

    public StudentController(AcademicService academicService,
                             UserService userService,
                             AssignmentService assignmentService,
                             QuizService quizService,
                             PracticalService practicalService,
                             MarksService marksService,
                             StudyMaterialService studyMaterialService,
                             FileStorageService fileStorageService) {
        this.academicService = academicService;
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.quizService = quizService;
        this.practicalService = practicalService;
        this.marksService = marksService;
        this.studyMaterialService = studyMaterialService;
        this.fileStorageService = fileStorageService;
    }

    private StudentProfile getStudent(UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        return userService.getStudentProfileByUser(user).orElseThrow();
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        List<Subject> mySubjects = enrollments.stream().map(Enrollment::getSubject).collect(Collectors.toList());

        List<Assignment> assignments = assignmentService.getAssignmentsForSubjects(mySubjects);
        List<AssignmentSubmission> mySubmissions = assignmentService.getSubmissionsByStudent(student);

        Set<Long> submittedAssignmentIds = mySubmissions.stream()
                .map(s -> s.getAssignment().getId()).collect(Collectors.toSet());

        List<Assignment> pendingAssignments = assignments.stream()
                .filter(a -> !submittedAssignmentIds.contains(a.getId()) && !a.isExpired())
                .collect(Collectors.toList());

        List<Quiz> quizzes = quizService.getQuizzesForSubjects(mySubjects);
        List<Practical> practicals = practicalService.getPracticalsForSubjects(mySubjects);

        Map<String, Object> performance = marksService.calculateStudentPerformance(student);
        List<StudentMark> recentMarks = marksService.getRecentMarksByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("mySubjects", mySubjects);
        model.addAttribute("pendingAssignments", pendingAssignments);
        model.addAttribute("submittedCount", mySubmissions.size());
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("practicals", practicals);
        model.addAttribute("recentMarks", recentMarks);
        model.addAttribute("performance", performance);

        return "student/dashboard";
    }

    // --- MY SUBJECTS ---
    @GetMapping("/subjects")
    public String listSubjects(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        model.addAttribute("enrollments", enrollments);
        return "student/subjects";
    }

    @GetMapping("/subjects/{id}")
    public String viewSubject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Subject subject = academicService.getSubjectById(id).orElseThrow();

        model.addAttribute("subject", subject);
        model.addAttribute("assignments", assignmentService.getAssignmentsBySubject(subject));
        model.addAttribute("quizzes", quizService.getQuizzesBySubject(subject));
        model.addAttribute("practicals", practicalService.getPracticalsBySubject(subject));
        model.addAttribute("materials", studyMaterialService.getMaterialsBySubject(subject));
        model.addAttribute("marks", marksService.getMarksByStudentAndSubject(student, subject));

        return "student/subject-detail";
    }

    // --- ASSIGNMENTS ---
    @GetMapping("/assignments")
    public String listAssignments(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        List<Subject> mySubjects = enrollments.stream().map(Enrollment::getSubject).collect(Collectors.toList());

        List<Assignment> assignments = assignmentService.getAssignmentsForSubjects(mySubjects);
        List<AssignmentSubmission> mySubmissions = assignmentService.getSubmissionsByStudent(student);

        Map<Long, AssignmentSubmission> submissionMap = new HashMap<>();
        for (AssignmentSubmission s : mySubmissions) {
            submissionMap.put(s.getAssignment().getId(), s);
        }

        model.addAttribute("assignments", assignments);
        model.addAttribute("submissionMap", submissionMap);
        return "student/assignments";
    }

    @GetMapping("/assignments/{id}")
    public String viewAssignmentDetails(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Assignment assignment = assignmentService.getAssignmentById(id).orElseThrow();
        Optional<AssignmentSubmission> submission = assignmentService.getSubmission(assignment, student);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission.orElse(null));
        return "student/assignment-detail";
    }

    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(required = false) String textContent,
                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            StudentProfile student = getStudent(userDetails);
            Assignment assignment = assignmentService.getAssignmentById(id).orElseThrow();

            String fileUrl = null;
            String fileName = null;
            if (file != null && !file.isEmpty()) {
                fileUrl = fileStorageService.storeFile(file);
                fileName = file.getOriginalFilename();
            }

            assignmentService.submitAssignment(assignment, student, textContent, fileUrl, fileName);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Submission failed: " + ex.getMessage());
        }
        return "redirect:/student/assignments/" + id;
    }

    // --- QUIZZES ---
    @GetMapping("/quizzes")
    public String listQuizzes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        List<Subject> mySubjects = enrollments.stream().map(Enrollment::getSubject).collect(Collectors.toList());

        List<Quiz> quizzes = quizService.getQuizzesForSubjects(mySubjects);
        List<QuizAttempt> myAttempts = quizService.getAttemptsByStudent(student);

        Map<Long, QuizAttempt> attemptMap = new HashMap<>();
        for (QuizAttempt qa : myAttempts) {
            attemptMap.put(qa.getQuiz().getId(), qa);
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("attemptMap", attemptMap);
        return "student/quizzes";
    }

    @GetMapping("/quizzes/{id}")
    public String viewQuizDetails(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        Optional<QuizAttempt> attempt = quizService.getAttempt(quiz, student);

        model.addAttribute("quiz", quiz);
        model.addAttribute("attempt", attempt.orElse(null));
        return "student/quiz-detail";
    }

    @PostMapping("/quizzes/{id}/start")
    public String startQuiz(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            StudentProfile student = getStudent(userDetails);
            Quiz quiz = quizService.getQuizById(id).orElseThrow();
            QuizAttempt attempt = quizService.startQuizAttempt(quiz, student);

            if (attempt.isCompleted()) {
                redirectAttributes.addFlashAttribute("infoMessage", "You have already completed this quiz.");
                return "redirect:/student/quizzes/" + id + "/result";
            }

            return "redirect:/student/quizzes/" + id + "/take";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/quizzes/" + id;
        }
    }

    @GetMapping("/quizzes/{id}/take")
    public String takeQuiz(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        StudentProfile student = getStudent(userDetails);
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        QuizAttempt attempt = quizService.getAttempt(quiz, student)
                .orElseThrow(() -> new IllegalStateException("Attempt not started"));

        if (attempt.isCompleted()) {
            return "redirect:/student/quizzes/" + id + "/result";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("attempt", attempt);
        model.addAttribute("questions", quiz.getQuestions());
        return "student/quiz-take";
    }

    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam Map<String, String> answersMap,
                             RedirectAttributes redirectAttributes) {
        try {
            StudentProfile student = getStudent(userDetails);
            Quiz quiz = quizService.getQuizById(id).orElseThrow();
            QuizAttempt attempt = quizService.getAttempt(quiz, student).orElseThrow();

            quizService.submitQuizAttempt(attempt.getId(), answersMap);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz submitted successfully!");
            return "redirect:/student/quizzes/" + id + "/result";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Submission failed: " + ex.getMessage());
            return "redirect:/student/quizzes/" + id;
        }
    }

    @GetMapping("/quizzes/{id}/result")
    public String viewQuizResult(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        QuizAttempt attempt = quizService.getAttempt(quiz, student).orElseThrow();

        model.addAttribute("quiz", quiz);
        model.addAttribute("attempt", attempt);
        model.addAttribute("answers", attempt.getAnswers());
        return "student/quiz-result";
    }

    // --- PRACTICALS / LABS ---
    @GetMapping("/practicals")
    public String listPracticals(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        List<Subject> mySubjects = enrollments.stream().map(Enrollment::getSubject).collect(Collectors.toList());

        List<Practical> practicals = practicalService.getPracticalsForSubjects(mySubjects);
        List<PracticalSubmission> mySubmissions = practicalService.getSubmissionsByStudent(student);

        Map<Long, PracticalSubmission> submissionMap = new HashMap<>();
        for (PracticalSubmission s : mySubmissions) {
            submissionMap.put(s.getPractical().getId(), s);
        }

        model.addAttribute("practicals", practicals);
        model.addAttribute("submissionMap", submissionMap);
        return "student/practicals";
    }

    @GetMapping("/practicals/{id}")
    public String viewPracticalDetails(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Practical practical = practicalService.getPracticalById(id).orElseThrow();
        Optional<PracticalSubmission> submission = practicalService.getSubmission(practical, student);

        model.addAttribute("practical", practical);
        model.addAttribute("submission", submission.orElse(null));
        return "student/practical-detail";
    }

    @PostMapping("/practicals/{id}/submit")
    public String submitPractical(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(required = false) String remarks,
                                  @RequestParam("reportFile") MultipartFile reportFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            StudentProfile student = getStudent(userDetails);
            Practical practical = practicalService.getPracticalById(id).orElseThrow();

            if (reportFile == null || reportFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a lab report file to upload.");
                return "redirect:/student/practicals/" + id;
            }

            String fileUrl = fileStorageService.storeFile(reportFile);
            practicalService.submitPractical(practical, student, fileUrl, reportFile.getOriginalFilename(), remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Lab report submitted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "redirect:/student/practicals/" + id;
    }

    // --- STUDY MATERIALS ---
    @GetMapping("/materials")
    public String listMaterials(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        List<Enrollment> enrollments = academicService.getEnrollmentsByStudent(student);
        List<Subject> mySubjects = enrollments.stream().map(Enrollment::getSubject).collect(Collectors.toList());

        List<StudyMaterial> allMaterials = new ArrayList<>();
        for (Subject s : mySubjects) {
            allMaterials.addAll(studyMaterialService.getMaterialsBySubject(s));
        }

        model.addAttribute("mySubjects", mySubjects);
        model.addAttribute("materials", allMaterials);
        return "student/materials";
    }

    // --- MY RESULTS & ACADEMIC PERFORMANCE ---
    @GetMapping("/results")
    public String myResults(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentProfile student = getStudent(userDetails);
        Map<String, Object> performance = marksService.calculateStudentPerformance(student);
        List<StudentMark> allMarks = marksService.getMarksByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("performance", performance);
        model.addAttribute("marksList", allMarks);
        return "student/results";
    }
}
