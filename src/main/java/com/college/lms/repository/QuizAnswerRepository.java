package com.college.lms.repository;

import com.college.lms.entity.QuizAnswer;
import com.college.lms.entity.QuizAttempt;
import com.college.lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByAttempt(QuizAttempt attempt);
    Optional<QuizAnswer> findByAttemptAndQuestion(QuizAttempt attempt, QuizQuestion question);
}
