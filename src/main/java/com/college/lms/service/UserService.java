package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.DepartmentRepository;
import com.college.lms.repository.StudentProfileRepository;
import com.college.lms.repository.TeacherProfileRepository;
import com.college.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TeacherProfileRepository teacherProfileRepository,
                       StudentProfileRepository studentProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllTeachers() {
        return userRepository.findByRole(Role.ROLE_TEACHER);
    }

    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.ROLE_STUDENT);
    }

    public List<TeacherProfile> getAllTeacherProfiles() {
        return teacherProfileRepository.findAll();
    }

    public List<StudentProfile> getAllStudentProfiles() {
        return studentProfileRepository.findAll();
    }

    public Optional<TeacherProfile> getTeacherProfileByUser(User user) {
        return teacherProfileRepository.findByUser(user);
    }

    public Optional<StudentProfile> getStudentProfileByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }

    public Optional<StudentProfile> getStudentProfileById(Long id) {
        return studentProfileRepository.findById(id);
    }

    public Optional<TeacherProfile> getTeacherProfileById(Long id) {
        return teacherProfileRepository.findById(id);
    }

    @Transactional
    public User createTeacher(String email, String rawPassword, String fullName, String phone,
                              String employeeId, String designation, String qualification,
                              String specialization, Department department) {
        User user = new User(email, passwordEncoder.encode(rawPassword), fullName, Role.ROLE_TEACHER);
        user.setPhone(phone);
        user = userRepository.save(user);

        TeacherProfile profile = new TeacherProfile(user, employeeId, designation, qualification, specialization, department);
        teacherProfileRepository.save(profile);
        return user;
    }

    @Transactional
    public User createStudent(String email, String rawPassword, String fullName, String phone,
                              String rollNumber, Department department, Course course,
                              int semester, String academicYear) {
        User user = new User(email, passwordEncoder.encode(rawPassword), fullName, Role.ROLE_STUDENT);
        user.setPhone(phone);
        user = userRepository.save(user);

        StudentProfile profile = new StudentProfile(user, rollNumber, department, course, semester, academicYear);
        studentProfileRepository.save(profile);
        return user;
    }

    @Transactional
    public void toggleUserActive(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(!user.isActive());
            userRepository.save(user);
        });
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updateUserProfile(User user, String fullName, String phone) {
        user.setFullName(fullName);
        user.setPhone(phone);
        userRepository.save(user);
    }
}
