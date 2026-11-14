package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AcademicService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public AcademicService(DepartmentRepository departmentRepository,
                           CourseRepository courseRepository,
                           SubjectRepository subjectRepository,
                           EnrollmentRepository enrollmentRepository,
                           StudentProfileRepository studentProfileRepository,
                           UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    @Transactional
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByDepartment(Department department) {
        return courseRepository.findByDepartment(department);
    }

    @Transactional
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getSubjectsByTeacher(User teacher) {
        return subjectRepository.findByTeacher(teacher);
    }

    public List<Subject> getSubjectsByCourse(Course course) {
        return subjectRepository.findByCourse(course);
    }

    @Transactional
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public List<Enrollment> getEnrollmentsBySubject(Subject subject) {
        return enrollmentRepository.findBySubject(subject);
    }

    public List<Enrollment> getEnrollmentsByStudent(StudentProfile student) {
        return enrollmentRepository.findByStudent(student);
    }

    @Transactional
    public Enrollment enrollStudent(StudentProfile student, Subject subject) {
        return enrollmentRepository.findByStudentAndSubject(student, subject)
                .orElseGet(() -> enrollmentRepository.save(new Enrollment(student, subject)));
    }

    @Transactional
    public void unenrollStudent(StudentProfile student, Subject subject) {
        enrollmentRepository.findByStudentAndSubject(student, subject)
                .ifPresent(enrollmentRepository::delete);
    }

    public long getTotalStudents() {
        return userRepository.countByRole(Role.ROLE_STUDENT);
    }

    public long getTotalTeachers() {
        return userRepository.countByRole(Role.ROLE_TEACHER);
    }

    public long getTotalCourses() {
        return courseRepository.count();
    }

    public long getTotalSubjects() {
        return subjectRepository.count();
    }
}
