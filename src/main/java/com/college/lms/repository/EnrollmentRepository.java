package com.college.lms.repository;

import com.college.lms.entity.Enrollment;
import com.college.lms.entity.StudentProfile;
import com.college.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(StudentProfile student);
    List<Enrollment> findBySubject(Subject subject);
    Optional<Enrollment> findByStudentAndSubject(StudentProfile student, Subject subject);
    boolean existsByStudentAndSubject(StudentProfile student, Subject subject);
    long countBySubject(Subject subject);
    long countByStudent(StudentProfile student);
}
