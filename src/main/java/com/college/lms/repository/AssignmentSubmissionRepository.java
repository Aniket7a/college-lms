package com.college.lms.repository;

import com.college.lms.entity.Assignment;
import com.college.lms.entity.AssignmentSubmission;
import com.college.lms.entity.StudentProfile;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);
    List<AssignmentSubmission> findByStudentOrderBySubmissionDateDesc(StudentProfile student);
    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, StudentProfile student);

    long countByAssignment(Assignment assignment);

    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.teacher = :teacher AND s.status IN ('SUBMITTED', 'LATE', 'UNDER_EVALUATION')")
    long countPendingEvaluationsByTeacher(User teacher);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.teacher = :teacher ORDER BY s.submissionDate DESC")
    List<AssignmentSubmission> findRecentSubmissionsByTeacher(User teacher);
}
