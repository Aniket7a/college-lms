package com.college.lms.repository;

import com.college.lms.entity.Assessment;
import com.college.lms.entity.StudentMark;
import com.college.lms.entity.StudentProfile;
import com.college.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentMarkRepository extends JpaRepository<StudentMark, Long> {
    List<StudentMark> findByAssessment(Assessment assessment);
    List<StudentMark> findByStudent(StudentProfile student);
    Optional<StudentMark> findByAssessmentAndStudent(Assessment assessment, StudentProfile student);

    @Query("SELECT m FROM StudentMark m WHERE m.student = :student AND m.assessment.subject = :subject")
    List<StudentMark> findByStudentAndSubject(StudentProfile student, Subject subject);

    @Query("SELECT m FROM StudentMark m WHERE m.student = :student ORDER BY m.assessment.assessmentDate DESC")
    List<StudentMark> findRecentMarksByStudent(StudentProfile student);
}
