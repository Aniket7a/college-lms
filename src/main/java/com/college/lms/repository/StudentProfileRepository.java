package com.college.lms.repository;

import com.college.lms.entity.Course;
import com.college.lms.entity.Department;
import com.college.lms.entity.StudentProfile;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByRollNumber(String rollNumber);
    List<StudentProfile> findByDepartment(Department department);
    List<StudentProfile> findByCourse(Course course);
    List<StudentProfile> findByCourseAndCurrentSemester(Course course, int semester);
    boolean existsByRollNumber(String rollNumber);
}
