package com.college.lms.repository;

import com.college.lms.entity.QuestionOption;
import com.college.lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionOrderByOrderIndexAsc(QuizQuestion question);
}
