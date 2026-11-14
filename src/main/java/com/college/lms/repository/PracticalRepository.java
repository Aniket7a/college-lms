package com.college.lms.repository;

import com.college.lms.entity.Practical;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticalRepository extends JpaRepository<Practical, Long> {
    List<Practical> findBySubjectOrderByExperimentNumberAsc(Subject subject);
    List<Practical> findByTeacherOrderByCreatedAtDesc(User teacher);
    List<Practical> findBySubjectInOrderByDueDateAsc(List<Subject> subjects);
}
