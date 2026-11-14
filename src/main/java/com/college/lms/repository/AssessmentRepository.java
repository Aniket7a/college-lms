package com.college.lms.repository;

import com.college.lms.entity.Assessment;
import com.college.lms.entity.AssessmentType;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findBySubjectOrderByAssessmentDateDesc(Subject subject);
    List<Assessment> findByTeacherOrderByAssessmentDateDesc(User teacher);
    List<Assessment> findBySubjectAndAssessmentType(Subject subject, AssessmentType assessmentType);
    List<Assessment> findBySubjectInOrderByAssessmentDateDesc(List<Subject> subjects);
}
