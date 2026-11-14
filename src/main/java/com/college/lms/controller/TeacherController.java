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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final AcademicService academicService;
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final QuizService quizService;
    private final PracticalService practicalService;
    private final MarksService marksService;
    private final StudyMaterialService studyMaterialService;
    private final FileStorageService fileStorageService;

    public TeacherController(AcademicService academicService,
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

    private User getTeacher(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        List<Subject> mySubjects = academicService.getSubjectsByTeacher(teacher);

        int totalStudents = 0;
        for (Subject s : mySubjects) {
            totalStudents += s.getEnrollments().size();
        }

        model.addAttribute("mySubjects", mySubjects);
        model.addAttribute("totalAssignedSubjects", mySubjects.size());
        model.addAttribute("totalEnrolledStudents", totalStudents);
        model.addAttribute("activeAssignments", assignmentService.countActiveAssignmentsByTeacher(teacher));
        model.addAttribute("pendingEvaluations", assignmentService.countPendingEvaluationsByTeacher(teacher));
        model.addAttribute("upcomingQuizzes", quizService.countUpcomingQuizzesByTeacher(teacher));
        model.addAttribute("recentSubmissions", assignmentService.getRecentSubmissionsByTeacher(teacher));

        return "teacher/dashboard";
    }

    // --- SUBJECTS ---
    @GetMapping("/subjects")
    public String listSubjects(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        model.addAttribute("subjects", academicService.getSubjectsByTeacher(teacher));
        return "teacher/subjects";
    }

    @GetMapping("/subjects/{id}")
    public String viewSubject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Subject subject = academicService.getSubjectById(id).orElseThrow();
        model.addAttribute("subject", subject);
        model.addAttribute("enrollments", academicService.getEnrollmentsBySubject(subject));
        model.addAttribute("assignments", assignmentService.getAssignmentsBySubject(subject));
        model.addAttribute("quizzes", quizService.getQuizzesBySubject(subject));
        model.addAttribute("practicals", practicalService.getPracticalsBySubject(subject));
        model.addAttribute("materials", studyMaterialService.getMaterialsBySubject(subject));
        return "teacher/subject-detail";
    }

    // --- ASSIGNMENTS ---
    @GetMapping("/assignments")
    public String listAssignments(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        model.addAttribute("assignments", assignmentService.getAssignmentsByTeacher(teacher));
        model.addAttribute("subjects", academicService.getSubjectsByTeacher(teacher));
        return "teacher/assignments";
    }

    @PostMapping("/assignments/create")
    public String createAssignment(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam Long subjectId,
                                   @RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam(required = false) String instructions,
                                   @RequestParam String dueDate,
                                   @RequestParam int maxMarks,
                                   @RequestParam String submissionType,
                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

            Assignment assignment = new Assignment();
            assignment.setSubject(subject);
            assignment.setTeacher(teacher);
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setInstructions(instructions);
            assignment.setDueDate(LocalDateTime.parse(dueDate));
            assignment.setMaxMarks(maxMarks);
            assignment.setSubmissionType(submissionType);

            if (file != null && !file.isEmpty()) {
                String fileUrl = fileStorageService.storeFile(file);
                assignment.setAttachmentUrl(fileUrl);
                assignment.setAttachmentName(file.getOriginalFilename());
            }

            assignmentService.saveAssignment(assignment);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment created and published to students.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating assignment: " + ex.getMessage());
        }
        return "redirect:/teacher/assignments";
    }

    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.getAssignmentById(id).orElseThrow();
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", assignmentService.getSubmissionsByAssignment(assignment));
        return "teacher/submissions";
    }

    @PostMapping("/submissions/{id}/evaluate")
    public String evaluateSubmission(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam Double marksObtained,
                                     @RequestParam(required = false) String feedback,
                                     RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            AssignmentSubmission sub = assignmentService.evaluateSubmission(id, marksObtained, feedback, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Submission evaluated successfully. Awarded " + marksObtained + " marks.");
            return "redirect:/teacher/assignments/" + sub.getAssignment().getId() + "/submissions";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Evaluation failed: " + ex.getMessage());
            return "redirect:/teacher/assignments";
        }
    }

    @PostMapping("/submissions/{id}/toggle-resubmit")
    public String toggleResubmit(@PathVariable Long id, @RequestParam Long assignmentId, RedirectAttributes redirectAttributes) {
        assignmentService.toggleResubmission(id);
        redirectAttributes.addFlashAttribute("successMessage", "Resubmission permission updated.");
        return "redirect:/teacher/assignments/" + assignmentId + "/submissions";
    }

    // --- QUIZZES ---
    @GetMapping("/quizzes")
    public String listQuizzes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        model.addAttribute("quizzes", quizService.getQuizzesByTeacher(teacher));
        model.addAttribute("subjects", academicService.getSubjectsByTeacher(teacher));
        return "teacher/quizzes";
    }

    @PostMapping("/quizzes/create")
    public String createQuiz(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam Long subjectId,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String instructions,
                             @RequestParam String startDateTime,
                             @RequestParam String endDateTime,
                             @RequestParam int durationMinutes,
                             @RequestParam int totalMarks,
                             @RequestParam int passingMarks,
                             RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

            Quiz quiz = new Quiz();
            quiz.setSubject(subject);
            quiz.setTeacher(teacher);
            quiz.setTitle(title);
            quiz.setDescription(description);
            quiz.setInstructions(instructions);
            quiz.setStartDateTime(LocalDateTime.parse(startDateTime));
            quiz.setEndDateTime(LocalDateTime.parse(endDateTime));
            quiz.setDurationMinutes(durationMinutes);
            quiz.setTotalMarks(totalMarks);
            quiz.setPassingMarks(passingMarks);
            quiz.setPublished(true);
            quiz.setResultsReleased(true);

            Quiz saved = quizService.saveQuiz(quiz);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz created. Add questions now.");
            return "redirect:/teacher/quizzes/" + saved.getId() + "/questions";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating quiz: " + ex.getMessage());
            return "redirect:/teacher/quizzes";
        }
    }

    @GetMapping("/quizzes/{id}/questions")
    public String manageQuizQuestions(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quiz.getQuestions());
        return "teacher/quiz-questions";
    }

    @PostMapping("/quizzes/{id}/questions/add")
    public String addQuestion(@PathVariable Long id,
                              @RequestParam String questionText,
                              @RequestParam QuestionType questionType,
                              @RequestParam int marks,
                              @RequestParam(required = false) String explanation,
                              @RequestParam(value = "optionText", required = false) List<String> optionTexts,
                              @RequestParam(value = "isCorrect", required = false) List<Integer> correctIndices,
                              RedirectAttributes redirectAttributes) {
        try {
            List<QuestionOption> options = new ArrayList<>();
            if (questionType == QuestionType.MCQ || questionType == QuestionType.MULTIPLE_SELECT) {
                if (optionTexts != null) {
                    for (int i = 0; i < optionTexts.size(); i++) {
                        String txt = optionTexts.get(i);
                        if (txt != null && !txt.trim().isEmpty()) {
                            boolean isCorr = (correctIndices != null && correctIndices.contains(i));
                            options.add(new QuestionOption(null, txt.trim(), isCorr, i + 1));
                        }
                    }
                }
            } else if (questionType == QuestionType.TRUE_FALSE) {
                boolean trueIsCorrect = (correctIndices != null && correctIndices.contains(0));
                options.add(new QuestionOption(null, "True", trueIsCorrect, 1));
                options.add(new QuestionOption(null, "False", !trueIsCorrect, 2));
            }

            quizService.addQuestion(id, questionText, questionType, marks, explanation, options);
            redirectAttributes.addFlashAttribute("successMessage", "Question added successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding question: " + ex.getMessage());
        }
        return "redirect:/teacher/quizzes/" + id + "/questions";
    }

    @GetMapping("/quizzes/{id}/attempts")
    public String viewQuizAttempts(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", quizService.getAttemptsByQuiz(quiz));
        return "teacher/quiz-attempts";
    }

    @PostMapping("/quizzes/{id}/toggle-release")
    public String toggleQuizResultsRelease(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.toggleReleaseResults(id);
        redirectAttributes.addFlashAttribute("successMessage", "Quiz result release setting updated.");
        return "redirect:/teacher/quizzes/" + id + "/attempts";
    }

    @GetMapping("/quizzes/attempt/{attemptId}/evaluate")
    public String evaluateSubjectiveAnswersPage(@PathVariable Long attemptId, Model model) {
        QuizAttempt attempt = quizService.getAttemptById(attemptId).orElseThrow();
        model.addAttribute("attempt", attempt);
        model.addAttribute("answers", attempt.getAnswers());
        return "teacher/quiz-evaluate";
    }

    @PostMapping("/quizzes/attempt/{attemptId}/evaluate")
    public String submitSubjectiveEvaluation(@PathVariable Long attemptId,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Map<String, String> allParams,
                                             RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Map<Long, Double> questionMarks = new HashMap<>();
            String feedback = allParams.get("feedback");

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("mark_")) {
                    Long qId = Long.parseLong(entry.getKey().substring(5));
                    Double mark = Double.parseDouble(entry.getValue());
                    questionMarks.put(qId, mark);
                }
            }

            QuizAttempt attempt = quizService.evaluateSubjectiveAnswers(attemptId, questionMarks, feedback, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz answers evaluated successfully.");
            return "redirect:/teacher/quizzes/" + attempt.getQuiz().getId() + "/attempts";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
            return "redirect:/teacher/quizzes";
        }
    }

    // --- PRACTICALS / LABS ---
    @GetMapping("/practicals")
    public String listPracticals(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        model.addAttribute("practicals", practicalService.getPracticalsByTeacher(teacher));
        model.addAttribute("subjects", academicService.getSubjectsByTeacher(teacher));
        return "teacher/practicals";
    }

    @PostMapping("/practicals/create")
    public String createPractical(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam Long subjectId,
                                  @RequestParam String experimentNumber,
                                  @RequestParam String title,
                                  @RequestParam String description,
                                  @RequestParam(required = false) String vivaQuestions,
                                  @RequestParam String dueDate,
                                  @RequestParam(value = "componentName", required = false) List<String> compNames,
                                  @RequestParam(value = "componentMarks", required = false) List<Integer> compMarks,
                                  @RequestParam(value = "manualFile", required = false) MultipartFile manualFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

            Practical practical = new Practical();
            practical.setSubject(subject);
            practical.setTeacher(teacher);
            practical.setExperimentNumber(experimentNumber);
            practical.setTitle(title);
            practical.setDescription(description);
            practical.setVivaQuestions(vivaQuestions);
            practical.setDueDate(LocalDateTime.parse(dueDate));

            if (manualFile != null && !manualFile.isEmpty()) {
                String fileUrl = fileStorageService.storeFile(manualFile);
                practical.setLabManualUrl(fileUrl);
                practical.setLabManualName(manualFile.getOriginalFilename());
            }

            int totalMarks = 0;
            List<PracticalRubricComponent> components = new ArrayList<>();
            if (compNames != null && compMarks != null) {
                for (int i = 0; i < compNames.size(); i++) {
                    String name = compNames.get(i);
                    Integer marks = compMarks.get(i);
                    if (name != null && !name.trim().isEmpty() && marks != null) {
                        components.add(new PracticalRubricComponent(null, name.trim(), marks, i + 1));
                        totalMarks += marks;
                    }
                }
            }

            // Default rubric if none provided
            if (components.isEmpty()) {
                components.add(new PracticalRubricComponent(null, "Experiment Execution", 10, 1));
                components.add(new PracticalRubricComponent(null, "Output / Result", 5, 2));
                components.add(new PracticalRubricComponent(null, "Viva Voce", 5, 3));
                components.add(new PracticalRubricComponent(null, "Lab Record", 10, 4));
                totalMarks = 30;
            }

            practical.setMaxMarks(totalMarks);
            practicalService.savePractical(practical, components);
            redirectAttributes.addFlashAttribute("successMessage", "Practical " + experimentNumber + " assigned successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating practical: " + ex.getMessage());
        }
        return "redirect:/teacher/practicals";
    }

    @GetMapping("/practicals/{id}/submissions")
    public String viewPracticalSubmissions(@PathVariable Long id, Model model) {
        Practical practical = practicalService.getPracticalById(id).orElseThrow();
        model.addAttribute("practical", practical);
        model.addAttribute("submissions", practicalService.getSubmissionsByPractical(practical));
        return "teacher/practical-submissions";
    }

    @PostMapping("/practicals/submissions/{id}/evaluate")
    public String evaluatePracticalSubmission(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam Map<String, String> allParams,
                                              RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Map<Long, Double> compScores = new HashMap<>();
            String feedback = allParams.get("feedback");

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("comp_")) {
                    Long compId = Long.parseLong(entry.getKey().substring(5));
                    Double val = Double.parseDouble(entry.getValue());
                    compScores.put(compId, val);
                }
            }

            PracticalSubmission sub = practicalService.evaluatePractical(id, compScores, feedback, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Practical evaluated. Total score: " + sub.getTotalMarksObtained());
            return "redirect:/teacher/practicals/" + sub.getPractical().getId() + "/submissions";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Evaluation failed: " + ex.getMessage());
            return "redirect:/teacher/practicals";
        }
    }

    // --- MARKS & EVALUATION MODULE ---
    @GetMapping("/marks")
    public String marksModule(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(required = false) Long subjectId,
                              @RequestParam(required = false) Long assessmentId,
                              Model model) {
        User teacher = getTeacher(userDetails);
        List<Subject> subjects = academicService.getSubjectsByTeacher(teacher);
        model.addAttribute("subjects", subjects);

        Subject selectedSubject = null;
        if (subjectId != null) {
            selectedSubject = academicService.getSubjectById(subjectId).orElse(null);
        } else if (!subjects.isEmpty()) {
            selectedSubject = subjects.get(0);
        }
        model.addAttribute("selectedSubject", selectedSubject);

        List<Assessment> assessments = (selectedSubject != null) ?
                marksService.getAssessmentsBySubject(selectedSubject) : Collections.emptyList();
        model.addAttribute("assessments", assessments);

        Assessment selectedAssessment = null;
        if (assessmentId != null) {
            selectedAssessment = marksService.getAssessmentById(assessmentId).orElse(null);
        } else if (!assessments.isEmpty()) {
            selectedAssessment = assessments.get(0);
        }
        model.addAttribute("selectedAssessment", selectedAssessment);

        if (selectedSubject != null && selectedAssessment != null) {
            List<Enrollment> enrollments = academicService.getEnrollmentsBySubject(selectedSubject);
            List<StudentMark> marks = marksService.getMarksByAssessment(selectedAssessment);

            Map<Long, StudentMark> studentMarksMap = new HashMap<>();
            for (StudentMark m : marks) {
                studentMarksMap.put(m.getStudent().getId(), m);
            }

            model.addAttribute("enrollments", enrollments);
            model.addAttribute("studentMarksMap", studentMarksMap);
        }

        return "teacher/marks";
    }

    @PostMapping("/marks/assessment/create")
    public String createAssessment(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam Long subjectId,
                                   @RequestParam String title,
                                   @RequestParam AssessmentType assessmentType,
                                   @RequestParam double maxMarks,
                                   @RequestParam String assessmentDate,
                                   RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

            Assessment assessment = new Assessment();
            assessment.setSubject(subject);
            assessment.setTeacher(teacher);
            assessment.setTitle(title);
            assessment.setAssessmentType(assessmentType);
            assessment.setMaxMarks(maxMarks);
            assessment.setAssessmentDate(LocalDate.parse(assessmentDate));

            marksService.saveAssessment(assessment);
            redirectAttributes.addFlashAttribute("successMessage", "Assessment entry created.");
            return "redirect:/teacher/marks?subjectId=" + subjectId + "&assessmentId=" + assessment.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
            return "redirect:/teacher/marks";
        }
    }

    @PostMapping("/marks/record")
    public String recordStudentMark(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam Long assessmentId,
                                    @RequestParam Long studentId,
                                    @RequestParam double marksObtained,
                                    @RequestParam(required = false) String teacherRemarks,
                                    @RequestParam(required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Assessment assessment = marksService.getAssessmentById(assessmentId).orElseThrow();
            StudentProfile student = userService.getStudentProfileById(studentId).orElseThrow();

            marksService.recordMark(assessment, student, marksObtained, teacherRemarks, reason, teacher);
            redirectAttributes.addFlashAttribute("successMessage", "Marks recorded for " + student.getUser().getFullName());
            return "redirect:/teacher/marks?subjectId=" + assessment.getSubject().getId() + "&assessmentId=" + assessmentId;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to record mark: " + ex.getMessage());
            return "redirect:/teacher/marks";
        }
    }

    // --- STUDY MATERIALS ---
    @GetMapping("/materials")
    public String listMaterials(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User teacher = getTeacher(userDetails);
        model.addAttribute("materials", studyMaterialService.getMaterialsByTeacher(teacher));
        model.addAttribute("subjects", academicService.getSubjectsByTeacher(teacher));
        return "teacher/materials";
    }

    @PostMapping("/materials/upload")
    public String uploadMaterial(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam Long subjectId,
                                 @RequestParam String title,
                                 @RequestParam(required = false) String description,
                                 @RequestParam String materialType,
                                 @RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            User teacher = getTeacher(userDetails);
            Subject subject = academicService.getSubjectById(subjectId).orElseThrow();

            if (file == null || file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid file to upload.");
                return "redirect:/teacher/materials";
            }

            String fileUrl = fileStorageService.storeFile(file);
            String fileSize = fileStorageService.formatFileSize(file.getSize());

            StudyMaterial material = new StudyMaterial(subject, teacher, title, description, fileUrl, file.getOriginalFilename(), fileSize, materialType);
            studyMaterialService.saveMaterial(material);

            redirectAttributes.addFlashAttribute("successMessage", "Study material uploaded and shared with students.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload error: " + ex.getMessage());
        }
        return "redirect:/teacher/materials";
    }
}
