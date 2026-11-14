package com.college.lms.repository;

import com.college.lms.entity.Quiz;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySubjectOrderByStartDateTimeDesc(Subject subject);
    List<Quiz> findByTeacherOrderByCreatedAtDesc(User teacher);
    List<Quiz> findBySubjectInOrderByStartDateTimeDesc(List<Subject> subjects);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.teacher = :teacher AND q.endDateTime > :now")
    long countUpcomingQuizzesByTeacher(User teacher, LocalDateTime now);
}
