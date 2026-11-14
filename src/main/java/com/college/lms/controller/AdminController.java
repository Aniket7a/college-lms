package com.college.lms.controller;

import com.college.lms.entity.*;
import com.college.lms.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AcademicService academicService;
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final MarksService marksService;
    private final QuizService quizService;

    public AdminController(AcademicService academicService,
                           UserService userService,
                           AssignmentService assignmentService,
                           MarksService marksService,
                           QuizService quizService) {
        this.academicService = academicService;
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.marksService = marksService;
        this.quizService = quizService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", academicService.getTotalStudents());
        model.addAttribute("totalTeachers", academicService.getTotalTeachers());
        model.addAttribute("totalCourses", academicService.getTotalCourses());
        model.addAttribute("totalSubjects", academicService.getTotalSubjects());
        model.addAttribute("activeAssignments", assignmentService.countActiveAssignments());
        model.addAttribute("totalSubmissions", assignmentService.getTotalSubmissions());

        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("recentStudents", userService.getAllStudentProfiles());
        model.addAttribute("recentTeachers", userService.getAllTeacherProfiles());
        model.addAttribute("subjects", academicService.getAllSubjects());

        return "admin/dashboard";
    }

    // --- STUDENT MANAGEMENT ---
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", userService.getAllStudentProfiles());
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("courses", academicService.getAllCourses());
        return "admin/students";
    }

    @PostMapping("/students/create")
    public String createStudent(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam(required = false) String phone,
                                @RequestParam String rollNumber,
                                @RequestParam Long departmentId,
                                @RequestParam Long courseId,
                                @RequestParam int semester,
                                @RequestParam String academicYear,
                                RedirectAttributes redirectAttributes) {
        try {
            Department dept = academicService.getDepartmentById(departmentId).orElseThrow();
            Course course = academicService.getCourseById(courseId).orElseThrow();
            userService.createStudent(email, password, fullName, phone, rollNumber, dept, course, semester, academicYear);
            redirectAttributes.addFlashAttribute("successMessage", "Student " + fullName + " registered successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create student: " + ex.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudentProfile(@PathVariable Long id, Model model) {
        StudentProfile student = userService.getStudentProfileById(id).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("enrollments", academicService.getEnrollmentsByStudent(student));
        model.addAttribute("performance", marksService.calculateStudentPerformance(student));
        model.addAttribute("allSubjects", academicService.getAllSubjects());
        return "admin/student-profile";
    }

    @PostMapping("/students/{id}/toggle-status")
    public String toggleStudentStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        StudentProfile student = userService.getStudentProfileById(id).orElseThrow();
        userService.toggleUserActive(student.getUser().getId());
        redirectAttributes.addFlashAttribute("successMessage", "Student status updated.");
        return "redirect:/admin/students";
    }

    // --- TEACHER MANAGEMENT ---
    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", userService.getAllTeacherProfiles());
        model.addAttribute("departments", academicService.getAllDepartments());
        return "admin/teachers";
    }

    @PostMapping("/teachers/create")
    public String createTeacher(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam(required = false) String phone,
                                @RequestParam String employeeId,
                                @RequestParam String designation,
                                @RequestParam String qualification,
                                @RequestParam String specialization,
                                @RequestParam Long departmentId,
                                RedirectAttributes redirectAttributes) {
        try {
            Department dept = academicService.getDepartmentById(departmentId).orElseThrow();
            userService.createTeacher(email, password, fullName, phone, employeeId, designation, qualification, specialization, dept);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + fullName + " registered successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create teacher: " + ex.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/{id}")
    public String viewTeacherProfile(@PathVariable Long id, Model model) {
        TeacherProfile teacher = userService.getTeacherProfileById(id).orElseThrow();
        model.addAttribute("teacher", teacher);
        model.addAttribute("assignedSubjects", academicService.getSubjectsByTeacher(teacher.getUser()));
        return "admin/teacher-profile";
    }

    @PostMapping("/teachers/{id}/toggle-status")
    public String toggleTeacherStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TeacherProfile teacher = userService.getTeacherProfileById(id).orElseThrow();
        userService.toggleUserActive(teacher.getUser().getId());
        redirectAttributes.addFlashAttribute("successMessage", "Teacher status updated.");
        return "redirect:/admin/teachers";
    }

    // --- COURSES & DEPARTMENTS ---
    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("departments", academicService.getAllDepartments());
        return "admin/courses";
    }

    @PostMapping("/courses/create")
    public String createCourse(@RequestParam String code,
                               @RequestParam String name,
                               @RequestParam int durationYears,
                               @RequestParam int totalSemesters,
                               @RequestParam Long departmentId,
                               RedirectAttributes redirectAttributes) {
        try {
            Department dept = academicService.getDepartmentById(departmentId).orElseThrow();
            academicService.saveCourse(new Course(code, name, durationYears, totalSemesters, dept));
            redirectAttributes.addFlashAttribute("successMessage", "Course " + name + " created successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // --- SUBJECTS ---
    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("teachers", userService.getAllTeachers());
        return "admin/subjects";
    }

    @PostMapping("/subjects/create")
    public String createSubject(@RequestParam String code,
                                @RequestParam String name,
                                @RequestParam int semester,
                                @RequestParam int credits,
                                @RequestParam(required = false) String description,
                                @RequestParam Long departmentId,
                                @RequestParam Long courseId,
                                @RequestParam(required = false) Long teacherId,
                                RedirectAttributes redirectAttributes) {
        try {
            Department dept = academicService.getDepartmentById(departmentId).orElseThrow();
            Course course = academicService.getCourseById(courseId).orElseThrow();
            User teacher = teacherId != null ? userService.findById(teacherId).orElse(null) : null;

            Subject subject = new Subject(code, name, semester, credits, dept, course, teacher);
            subject.setDescription(description);
            academicService.saveSubject(subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject " + name + " added successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating subject: " + ex.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/{id}/assign-teacher")
    public String assignTeacherToSubject(@PathVariable Long id,
                                         @RequestParam Long teacherId,
                                         RedirectAttributes redirectAttributes) {
        Subject subject = academicService.getSubjectById(id).orElseThrow();
        User teacher = userService.findById(teacherId).orElseThrow();
        subject.setTeacher(teacher);
        academicService.saveSubject(subject);
        redirectAttributes.addFlashAttribute("successMessage", "Assigned " + teacher.getFullName() + " to " + subject.getName());
        return "redirect:/admin/subjects";
    }

    // --- ENROLLMENTS ---
    @GetMapping("/enrollments")
    public String listEnrollments(Model model) {
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("students", userService.getAllStudentProfiles());
        return "admin/enrollments";
    }

    @PostMapping("/enrollments/assign")
    public String assignStudentToSubject(@RequestParam Long studentId,
                                         @RequestParam Long subjectId,
                                         RedirectAttributes redirectAttributes) {
        StudentProfile student = userService.getStudentProfileById(studentId).orElseThrow();
        Subject subject = academicService.getSubjectById(subjectId).orElseThrow();
        academicService.enrollStudent(student, subject);
        redirectAttributes.addFlashAttribute("successMessage", "Enrolled " + student.getUser().getFullName() + " in " + subject.getName());
        return "redirect:/admin/enrollments";
    }

    // --- REPORTS ---
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("students", userService.getAllStudentProfiles());
        model.addAttribute("subjects", academicService.getAllSubjects());
        model.addAttribute("teachers", userService.getAllTeacherProfiles());
        model.addAttribute("totalSubmissions", assignmentService.getTotalSubmissions());
        model.addAttribute("activeAssignments", assignmentService.countActiveAssignments());
        return "admin/reports";
    }
}
