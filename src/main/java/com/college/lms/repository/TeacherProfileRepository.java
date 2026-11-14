package com.college.lms.repository;

import com.college.lms.entity.Department;
import com.college.lms.entity.TeacherProfile;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
    Optional<TeacherProfile> findByUser(User user);
    Optional<TeacherProfile> findByUserId(Long userId);
    Optional<TeacherProfile> findByEmployeeId(String employeeId);
    List<TeacherProfile> findByDepartment(Department department);
    boolean existsByEmployeeId(String employeeId);
}
