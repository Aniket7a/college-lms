package com.college.lms.repository;

import com.college.lms.entity.Assignment;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySubjectOrderByDueDateAsc(Subject subject);
    List<Assignment> findByTeacherOrderByDueDateDesc(User teacher);
    List<Assignment> findBySubjectInOrderByDueDateDesc(List<Subject> subjects);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.dueDate > :now")
    long countActiveAssignments(LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.teacher = :teacher AND a.dueDate > :now")
    long countActiveAssignmentsByTeacher(User teacher, LocalDateTime now);
}
