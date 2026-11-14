package com.college.lms.repository;

import com.college.lms.entity.Course;
import com.college.lms.entity.Department;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    List<Subject> findByTeacher(User teacher);
    List<Subject> findByDepartment(Department department);
    List<Subject> findByCourse(Course course);
    List<Subject> findByCourseAndSemester(Course course, int semester);
    boolean existsByCode(String code);
    long countByTeacher(User teacher);
}
