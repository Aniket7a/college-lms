package com.college.lms.repository;

import com.college.lms.entity.Quiz;
import com.college.lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizOrderByOrderIndexAsc(Quiz quiz);
}
