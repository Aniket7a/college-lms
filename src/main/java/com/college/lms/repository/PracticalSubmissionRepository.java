package com.college.lms.repository;

import com.college.lms.entity.Practical;
import com.college.lms.entity.PracticalSubmission;
import com.college.lms.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticalSubmissionRepository extends JpaRepository<PracticalSubmission, Long> {
    List<PracticalSubmission> findByPractical(Practical practical);
    List<PracticalSubmission> findByStudentOrderBySubmissionDateDesc(StudentProfile student);
    Optional<PracticalSubmission> findByPracticalAndStudent(Practical practical, StudentProfile student);
    boolean existsByPracticalAndStudent(Practical practical, StudentProfile student);
}
