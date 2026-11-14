package com.college.lms.repository;

import com.college.lms.entity.Quiz;
import com.college.lms.entity.QuizAttempt;
import com.college.lms.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuiz(Quiz quiz);
    List<QuizAttempt> findByStudentOrderByStartTimeDesc(StudentProfile student);
    Optional<QuizAttempt> findByQuizAndStudent(Quiz quiz, StudentProfile student);
    boolean existsByQuizAndStudent(Quiz quiz, StudentProfile student);
}
