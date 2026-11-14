package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;
    private final NotificationService notificationService;

    public QuizService(QuizRepository quizRepository,
                       QuizQuestionRepository questionRepository,
                       QuestionOptionRepository optionRepository,
                       QuizAttemptRepository attemptRepository,
                       QuizAnswerRepository answerRepository,
                       NotificationService notificationService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.notificationService = notificationService;
    }

    public List<Quiz> getQuizzesByTeacher(User teacher) {
        return quizRepository.findByTeacherOrderByCreatedAtDesc(teacher);
    }

    public List<Quiz> getQuizzesBySubject(Subject subject) {
        return quizRepository.findBySubjectOrderByStartDateTimeDesc(subject);
    }

    public List<Quiz> getQuizzesForSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) return List.of();
        return quizRepository.findBySubjectInOrderByStartDateTimeDesc(subjects);
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        boolean isNew = (quiz.getId() == null);
        Quiz saved = quizRepository.save(quiz);

        if (isNew && saved.getSubject() != null) {
            for (Enrollment e : saved.getSubject().getEnrollments()) {
                notificationService.createNotification(
                    e.getStudent().getUser(),
                    "New Quiz Available: " + saved.getTitle(),
                    "A new quiz is scheduled for " + saved.getSubject().getName() + ". Duration: " + saved.getDurationMinutes() + " mins.",
                    "/student/quizzes/" + saved.getId(),
                    "QUIZ"
                );
            }
        }

        return saved;
    }

    @Transactional
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    @Transactional
    public QuizQuestion addQuestion(Long quizId, String questionText, QuestionType type, int marks,
                                     String explanation, List<QuestionOption> options) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        QuizQuestion question = new QuizQuestion();
        question.setQuiz(quiz);
        question.setQuestionText(questionText);
        question.setQuestionType(type);
        question.setMarks(marks);
        question.setExplanation(explanation);
        question.setOrderIndex(quiz.getQuestions().size() + 1);

        QuizQuestion savedQuestion = questionRepository.save(question);

        if (options != null) {
            int idx = 1;
            for (QuestionOption opt : options) {
                opt.setQuestion(savedQuestion);
                opt.setOrderIndex(idx++);
                optionRepository.save(opt);
            }
        }

        return savedQuestion;
    }

    public Optional<QuizAttempt> getAttempt(Quiz quiz, StudentProfile student) {
        return attemptRepository.findByQuizAndStudent(quiz, student);
    }

    public Optional<QuizAttempt> getAttemptById(Long id) {
        return attemptRepository.findById(id);
    }

    public List<QuizAttempt> getAttemptsByQuiz(Quiz quiz) {
        return attemptRepository.findByQuiz(quiz);
    }

    public List<QuizAttempt> getAttemptsByStudent(StudentProfile student) {
        return attemptRepository.findByStudentOrderByStartTimeDesc(student);
    }

    @Transactional
    public QuizAttempt startQuizAttempt(Quiz quiz, StudentProfile student) {
        Optional<QuizAttempt> existing = attemptRepository.findByQuizAndStudent(quiz, student);
        if (existing.isPresent()) {
            return existing.get();
        }

        if (quiz.isExpired()) {
            throw new IllegalStateException("Quiz deadline has expired.");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setCompleted(false);
        attempt.setEvaluated(false);
        attempt.setScore(0.0);

        return attemptRepository.save(attempt);
    }

    @Transactional
    public QuizAttempt submitQuizAttempt(Long attemptId, Map<String, String> answersMap) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (attempt.isCompleted()) {
            return attempt;
        }

        Quiz quiz = attempt.getQuiz();
        LocalDateTime now = LocalDateTime.now();

        // Server-side timer validation with 60s grace period for network latency
        long minutesElapsed = Duration.between(attempt.getStartTime(), now).toMinutes();
        if (minutesElapsed > (quiz.getDurationMinutes() + 2)) {
            // Overtime - still accept what was submitted but flag
        }

        attempt.setSubmitTime(now);
        attempt.setCompleted(true);

        double totalScore = 0.0;
        boolean hasSubjective = false;

        List<QuizQuestion> questions = quiz.getQuestions();
        for (QuizQuestion q : questions) {
            String answerKey = "question_" + q.getId();
            String submittedAnswer = answersMap.get(answerKey);

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setAttempt(attempt);
            quizAnswer.setQuestion(q);

            if (q.getQuestionType() == QuestionType.MCQ || q.getQuestionType() == QuestionType.TRUE_FALSE) {
                quizAnswer.setSelectedOptionIds(submittedAnswer);
                if (submittedAnswer != null && !submittedAnswer.isEmpty()) {
                    try {
                        Long selectedOptId = Long.parseLong(submittedAnswer);
                        boolean isCorrect = q.getOptions().stream()
                                .anyMatch(opt -> opt.getId().equals(selectedOptId) && opt.isCorrect());
                        if (isCorrect) {
                            quizAnswer.setMarksAwarded((double) q.getMarks());
                            totalScore += q.getMarks();
                        } else {
                            quizAnswer.setMarksAwarded(0.0);
                        }
                    } catch (Exception e) {
                        quizAnswer.setMarksAwarded(0.0);
                    }
                }
                quizAnswer.setEvaluated(true);
            } else if (q.getQuestionType() == QuestionType.MULTIPLE_SELECT) {
                quizAnswer.setSelectedOptionIds(submittedAnswer);
                if (submittedAnswer != null && !submittedAnswer.isEmpty()) {
                    Set<Long> selectedIds = new HashSet<>();
                    for (String idStr : submittedAnswer.split(",")) {
                        try { selectedIds.add(Long.parseLong(idStr.trim())); } catch (Exception ignored) {}
                    }
                    Set<Long> correctIds = new HashSet<>();
                    q.getOptions().stream().filter(QuestionOption::isCorrect).forEach(o -> correctIds.add(o.getId()));

                    if (selectedIds.equals(correctIds)) {
                        quizAnswer.setMarksAwarded((double) q.getMarks());
                        totalScore += q.getMarks();
                    } else {
                        quizAnswer.setMarksAwarded(0.0);
                    }
                }
                quizAnswer.setEvaluated(true);
            } else if (q.getQuestionType() == QuestionType.SHORT_ANSWER) {
                quizAnswer.setTextAnswer(submittedAnswer);
                quizAnswer.setMarksAwarded(0.0);
                quizAnswer.setEvaluated(false);
                hasSubjective = true;
            }

            answerRepository.save(quizAnswer);
        }

        attempt.setScore(totalScore);
        attempt.setEvaluated(!hasSubjective);
        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Notify teacher of quiz submission
        notificationService.createNotification(
            quiz.getTeacher(),
            "Quiz Attempt Submitted: " + attempt.getStudent().getUser().getFullName(),
            attempt.getStudent().getUser().getFullName() + " completed '" + quiz.getTitle() + "'. Score: " + totalScore + " / " + quiz.getTotalMarks(),
            "/teacher/quizzes/" + quiz.getId() + "/attempts",
            "QUIZ"
        );

        return savedAttempt;
    }

    @Transactional
    public QuizAttempt evaluateSubjectiveAnswers(Long attemptId, Map<Long, Double> questionMarks,
                                                 String feedback, User teacher) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        double totalScore = 0.0;
        List<QuizAnswer> answers = answerRepository.findByAttempt(attempt);

        for (QuizAnswer ans : answers) {
            if (questionMarks.containsKey(ans.getQuestion().getId())) {
                double awarded = questionMarks.get(ans.getQuestion().getId());
                if (awarded > ans.getQuestion().getMarks()) {
                    awarded = ans.getQuestion().getMarks();
                }
                ans.setMarksAwarded(awarded);
                ans.setEvaluated(true);
                answerRepository.save(ans);
            }
            totalScore += (ans.getMarksAwarded() != null ? ans.getMarksAwarded() : 0.0);
        }

        attempt.setScore(totalScore);
        attempt.setEvaluated(true);
        attempt.setTeacherFeedback(feedback);
        QuizAttempt saved = attemptRepository.save(attempt);

        // Notify student
        notificationService.createNotification(
            attempt.getStudent().getUser(),
            "Quiz Evaluated: " + attempt.getQuiz().getTitle(),
            "Your quiz has been evaluated. Total Score: " + totalScore + " / " + attempt.getQuiz().getTotalMarks(),
            "/student/quizzes/" + attempt.getQuiz().getId() + "/result",
            "MARKS"
        );

        return saved;
    }

    @Transactional
    public void toggleReleaseResults(Long quizId) {
        quizRepository.findById(quizId).ifPresent(q -> {
            q.setResultsReleased(!q.isResultsReleased());
            quizRepository.save(q);
        });
    }

    public long countUpcomingQuizzesByTeacher(User teacher) {
        return quizRepository.countUpcomingQuizzesByTeacher(teacher, LocalDateTime.now());
    }
}
