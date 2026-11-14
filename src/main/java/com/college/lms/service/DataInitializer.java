package com.college.lms.service;

import com.college.lms.entity.*;
import com.college.lms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;
    private final PracticalRepository practicalRepository;
    private final PracticalRubricComponentRepository rubricRepository;
    private final PracticalSubmissionRepository practicalSubmissionRepository;
    private final PracticalComponentScoreRepository practicalScoreRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentMarkRepository studentMarkRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           CourseRepository courseRepository,
                           SubjectRepository subjectRepository,
                           TeacherProfileRepository teacherProfileRepository,
                           StudentProfileRepository studentProfileRepository,
                           EnrollmentRepository enrollmentRepository,
                           AssignmentRepository assignmentRepository,
                           AssignmentSubmissionRepository submissionRepository,
                           QuizRepository quizRepository,
                           QuizQuestionRepository questionRepository,
                           QuestionOptionRepository optionRepository,
                           QuizAttemptRepository attemptRepository,
                           QuizAnswerRepository answerRepository,
                           PracticalRepository practicalRepository,
                           PracticalRubricComponentRepository rubricRepository,
                           PracticalSubmissionRepository practicalSubmissionRepository,
                           PracticalComponentScoreRepository practicalScoreRepository,
                           AssessmentRepository assessmentRepository,
                           StudentMarkRepository studentMarkRepository,
                           StudyMaterialRepository studyMaterialRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.practicalRepository = practicalRepository;
        this.rubricRepository = rubricRepository;
        this.practicalSubmissionRepository = practicalSubmissionRepository;
        this.practicalScoreRepository = practicalScoreRepository;
        this.assessmentRepository = assessmentRepository;
        this.studentMarkRepository = studentMarkRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println("Initializing College LMS Demo Data...");

        // 1. Departments
        Department cseDept = departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Department focusing on software engineering, algorithms, AI, and systems computing."));
        Department itDept = departmentRepository.save(new Department("IT", "Information Technology", "Department focusing on cloud systems, cybersecurity, network architecture and web engineering."));
        Department eceDept = departmentRepository.save(new Department("ECE", "Electronics & Communication", "Department focusing on microelectronics, embedded systems, and communication."));

        // 2. Courses
        Course btechCse = courseRepository.save(new Course("BTECH-CSE", "Bachelor of Technology in Computer Science", 4, 8, cseDept));
        Course btechIt = courseRepository.save(new Course("BTECH-IT", "Bachelor of Technology in Information Technology", 4, 8, itDept));

        // 3. Admin User
        User admin = new User("admin@college.edu", passwordEncoder.encode("admin123"), "Dean Robert Harrison", Role.ROLE_ADMIN);
        admin.setPhone("+1-555-0199");
        admin = userRepository.save(admin);

        // 4. Teacher Users & Profiles
        User teacher1 = new User("dr.sharma@college.edu", passwordEncoder.encode("teacher123"), "Dr. Rajesh Sharma", Role.ROLE_TEACHER);
        teacher1.setPhone("+1-555-0142");
        teacher1 = userRepository.save(teacher1);
        TeacherProfile teacherProfile1 = teacherProfileRepository.save(
            new TeacherProfile(teacher1, "EMP-CSE-101", "Professor & Head of Dept", "Ph.D. in Computer Science (Stanford)", "Algorithms, Data Structures, Distributed Systems", cseDept)
        );

        User teacher2 = new User("prof.smith@college.edu", passwordEncoder.encode("teacher123"), "Prof. Emily Smith", Role.ROLE_TEACHER);
        teacher2.setPhone("+1-555-0177");
        teacher2 = userRepository.save(teacher2);
        TeacherProfile teacherProfile2 = teacherProfileRepository.save(
            new TeacherProfile(teacher2, "EMP-CSE-102", "Associate Professor", "M.Tech in Database Systems (MIT)", "Database Management, Cloud Data, NoSQL", cseDept)
        );

        // 5. Student Users & Profiles
        User studentUser1 = new User("student1@college.edu", passwordEncoder.encode("student123"), "Aarav Patel", Role.ROLE_STUDENT);
        studentUser1.setPhone("+1-555-0211");
        studentUser1 = userRepository.save(studentUser1);
        StudentProfile studentProfile1 = studentProfileRepository.save(
            new StudentProfile(studentUser1, "2024-CS-001", cseDept, btechCse, 3, "2026-2027")
        );

        User studentUser2 = new User("student2@college.edu", passwordEncoder.encode("student123"), "Priya Sharma", Role.ROLE_STUDENT);
        studentUser2.setPhone("+1-555-0222");
        studentUser2 = userRepository.save(studentUser2);
        StudentProfile studentProfile2 = studentProfileRepository.save(
            new StudentProfile(studentUser2, "2024-CS-002", cseDept, btechCse, 3, "2026-2027")
        );

        User studentUser3 = new User("student3@college.edu", passwordEncoder.encode("student123"), "Rahul Verma", Role.ROLE_STUDENT);
        studentUser3.setPhone("+1-555-0233");
        studentUser3 = userRepository.save(studentUser3);
        StudentProfile studentProfile3 = studentProfileRepository.save(
            new StudentProfile(studentUser3, "2024-IT-001", itDept, btechIt, 3, "2026-2027")
        );

        // 6. Subjects
        Subject dsa = new Subject("CS301", "Data Structures & Algorithms", 3, 4, cseDept, btechCse, teacher1);
        dsa.setDescription("Comprehensive study of linear and non-linear data structures, asymptotic notation, sorting, tree balances, and graph algorithms.");
        dsa = subjectRepository.save(dsa);

        Subject dbms = new Subject("CS302", "Database Management Systems", 3, 4, cseDept, btechCse, teacher2);
        dbms.setDescription("Relational algebra, ER modeling, SQL queries, normalization, ACID properties, transaction processing, and indexing.");
        dbms = subjectRepository.save(dbms);

        Subject webTech = new Subject("CS303", "Web Technologies & Cloud", 3, 3, cseDept, btechCse, teacher1);
        webTech.setDescription("Modern enterprise web applications, Spring Boot REST APIs, Thymeleaf, frontend frameworks, security, and cloud deployment.");
        webTech = subjectRepository.save(webTech);

        Subject compNet = new Subject("IT301", "Computer Networks", 3, 4, itDept, btechIt, teacher2);
        compNet.setDescription("OSI and TCP/IP protocol suites, IP addressing, routing algorithms, transport layer protocols, socket programming.");
        compNet = subjectRepository.save(compNet);

        // 7. Enrollments
        enrollmentRepository.save(new Enrollment(studentProfile1, dsa));
        enrollmentRepository.save(new Enrollment(studentProfile1, dbms));
        enrollmentRepository.save(new Enrollment(studentProfile1, webTech));

        enrollmentRepository.save(new Enrollment(studentProfile2, dsa));
        enrollmentRepository.save(new Enrollment(studentProfile2, dbms));
        enrollmentRepository.save(new Enrollment(studentProfile2, webTech));

        enrollmentRepository.save(new Enrollment(studentProfile3, compNet));
        enrollmentRepository.save(new Enrollment(studentProfile3, dbms));

        // 8. Study Materials
        studyMaterialRepository.save(new StudyMaterial(
            dsa, teacher1, "Lecture 01-04: Asymptotic Analysis & Recurrences",
            "Detailed lecture slides covering Big-O, Big-Omega, Big-Theta, and the Master Theorem for divide-and-conquer algorithms.",
            "/uploads/demo-dsa-analysis.pdf", "DSA-Module-1-Asymptotics.pdf", "2.4 MB", "LECTURE_SLIDE"
        ));
        studyMaterialRepository.save(new StudyMaterial(
            dsa, teacher1, "Binary Search Trees & AVL Rotations Complete Guide",
            "Comprehensive textbook chapter explaining BST operations, deletion cases, AVL tree height balances, and double rotations.",
            "/uploads/demo-bst-avl.pdf", "BST-and-AVL-Notes.pdf", "4.1 MB", "NOTES"
        ));
        studyMaterialRepository.save(new StudyMaterial(
            dbms, teacher2, "SQL Relational Algebra & Advanced Query Optimization",
            "Covers relational operators, selection push-down, B-Tree indexing, execution plans, and query performance tuning.",
            "/uploads/demo-dbms-sql.pdf", "DBMS-SQL-Optimization.pdf", "3.2 MB", "NOTES"
        ));
        studyMaterialRepository.save(new StudyMaterial(
            webTech, teacher1, "Spring Boot 3 & Security Architecture Guide",
            "Full architectural reference for Spring Security filter chains, BCrypt hashing, MVC pattern, and Thymeleaf integration.",
            "/uploads/demo-spring-boot.pdf", "SpringBoot-Architecture-Guide.pdf", "5.6 MB", "REFERENCE_BOOK"
        ));

        // 9. Assignments
        Assignment assign1 = new Assignment();
        assign1.setSubject(dsa);
        assign1.setTeacher(teacher1);
        assign1.setTitle("Assignment 1: AVL Tree Implementation with Self-Balancing");
        assign1.setDescription("Design and implement an AVL Tree in Java or C++ supporting insertion, deletion, searching, and in-order traversal.");
        assign1.setInstructions("1. Provide clean code with comments.\n2. Include rotation cases (LL, RR, LR, RL).\n3. Submit source files (.zip or .java) along with a brief execution screenshot report.");
        assign1.setStartDate(LocalDateTime.now().minusDays(5));
        assign1.setDueDate(LocalDateTime.now().plusDays(7));
        assign1.setMaxMarks(20);
        assign1.setSubmissionType("BOTH");
        assign1 = assignmentRepository.save(assign1);

        Assignment assign2 = new Assignment();
        assign2.setSubject(dbms);
        assign2.setTeacher(teacher2);
        assign2.setTitle("Assignment 2: Schema Normalization & Complex SQL Queries");
        assign2.setDescription("Normalize an unnormalized university dataset into 1NF, 2NF, 3NF, and BCNF. Write 10 complex SQL queries with joins, grouping, and subqueries.");
        assign2.setInstructions("Submit a PDF detailing the functional dependencies, candidate keys, and step-by-step normalization decomposition.");
        assign2.setStartDate(LocalDateTime.now().minusDays(3));
        assign2.setDueDate(LocalDateTime.now().plusDays(5));
        assign2.setMaxMarks(25);
        assign2.setSubmissionType("FILE");
        assign2 = assignmentRepository.save(assign2);

        Assignment assign3 = new Assignment();
        assign3.setSubject(webTech);
        assign3.setTeacher(teacher1);
        assign3.setTitle("Assignment 3: RESTful API Development with Spring Boot");
        assign3.setDescription("Construct a complete CRUD REST service with DTO validation, JPA repository layer, and Spring Security token or session protection.");
        assign3.setInstructions("Submit your GitHub repository link and a PDF containing your API endpoint documentation (OpenAPI or Postman collection).");
        assign3.setStartDate(LocalDateTime.now().minusDays(1));
        assign3.setDueDate(LocalDateTime.now().plusDays(10));
        assign3.setMaxMarks(30);
        assign3.setSubmissionType("BOTH");
        assign3 = assignmentRepository.save(assign3);

        // 10. Assignment Submissions
        // Aarav submitted Assignment 1 and Dr. Sharma evaluated it
        AssignmentSubmission sub1 = new AssignmentSubmission();
        sub1.setAssignment(assign1);
        sub1.setStudent(studentProfile1);
        sub1.setSubmissionDate(LocalDateTime.now().minusDays(2));
        sub1.setTextContent("Implemented AVL Tree with generic keys in Java. All 4 rotation cases tested and verified. In-order traversal produces sorted output in O(n) time.");
        sub1.setFileName("Aarav_Patel_AVL_Tree.zip");
        sub1.setFileUrl("/uploads/Aarav_Patel_AVL_Tree.zip");
        sub1.setStatus("EVALUATED");
        sub1.setMarksObtained(18.5);
        sub1.setFeedback("Excellent implementation and clean code structure! Tree balance factors and edge case deletions handled gracefully.");
        sub1.setEvaluatedBy(teacher1);
        sub1.setEvaluatedAt(LocalDateTime.now().minusDays(1));
        submissionRepository.save(sub1);

        // Priya submitted Assignment 1 (Under evaluation)
        AssignmentSubmission sub2 = new AssignmentSubmission();
        sub2.setAssignment(assign1);
        sub2.setStudent(studentProfile2);
        sub2.setSubmissionDate(LocalDateTime.now().minusHours(8));
        sub2.setTextContent("AVL implementation completed in C++. Double rotations tested on 1,000 randomized inputs.");
        sub2.setFileName("Priya_Sharma_AVL.zip");
        sub2.setFileUrl("/uploads/Priya_Sharma_AVL.zip");
        sub2.setStatus("SUBMITTED");
        submissionRepository.save(sub2);

        // Rahul submitted Assignment 2
        AssignmentSubmission sub3 = new AssignmentSubmission();
        sub3.setAssignment(assign2);
        sub3.setStudent(studentProfile3);
        sub3.setSubmissionDate(LocalDateTime.now().minusDays(1));
        sub3.setFileName("Rahul_Verma_DBMS_Normalization.pdf");
        sub3.setFileUrl("/uploads/Rahul_Verma_DBMS_Normalization.pdf");
        sub3.setStatus("SUBMITTED");
        submissionRepository.save(sub3);

        // 11. Quizzes
        Quiz quiz1 = new Quiz();
        quiz1.setSubject(dsa);
        quiz1.setTeacher(teacher1);
        quiz1.setTitle("DSA Mid-Term Online Assessment");
        quiz1.setDescription("Online quiz covering time complexity, stacks, queues, binary trees, and binary search algorithms.");
        quiz1.setInstructions("Answer all questions. Once submitted, answers cannot be edited. Timer begins when you click 'Start Quiz'.");
        quiz1.setStartDateTime(LocalDateTime.now().minusDays(2));
        quiz1.setEndDateTime(LocalDateTime.now().plusDays(5));
        quiz1.setDurationMinutes(20);
        quiz1.setTotalMarks(20);
        quiz1.setPassingMarks(8);
        quiz1.setPublished(true);
        quiz1.setResultsReleased(true);
        quiz1 = quizRepository.save(quiz1);

        // Add Questions to Quiz 1
        // Q1: MCQ
        QuizQuestion q1 = new QuizQuestion();
        q1.setQuiz(quiz1);
        q1.setQuestionText("What is the worst-case time complexity of searching for an element in an unbalanced Binary Search Tree?");
        q1.setQuestionType(QuestionType.MCQ);
        q1.setMarks(5);
        q1.setOrderIndex(1);
        q1.setExplanation("In the worst case (a skewed tree), the tree degenerates into a linked list of height N, resulting in O(N) search time.");
        q1 = questionRepository.save(q1);

        optionRepository.save(new QuestionOption(q1, "O(log N)", false, 1));
        optionRepository.save(new QuestionOption(q1, "O(1)", false, 2));
        QuestionOption q1Correct = optionRepository.save(new QuestionOption(q1, "O(N)", true, 3));
        optionRepository.save(new QuestionOption(q1, "O(N log N)", false, 4));

        // Q2: True/False
        QuizQuestion q2 = new QuizQuestion();
        q2.setQuiz(quiz1);
        q2.setQuestionText("In an AVL tree, the balance factor of any node must be either -1, 0, or +1.");
        q2.setQuestionType(QuestionType.TRUE_FALSE);
        q2.setMarks(5);
        q2.setOrderIndex(2);
        q2.setExplanation("True. By definition, an AVL tree requires the height difference between left and right subtrees to be at most 1.");
        q2 = questionRepository.save(q2);

        QuestionOption q2Correct = optionRepository.save(new QuestionOption(q2, "True", true, 1));
        optionRepository.save(new QuestionOption(q2, "False", false, 2));

        // Q3: Multiple Select
        QuizQuestion q3 = new QuizQuestion();
        q3.setQuiz(quiz1);
        q3.setQuestionText("Which of the following data structures can be used to implement a Priority Queue? (Select all that apply)");
        q3.setQuestionType(QuestionType.MULTIPLE_SELECT);
        q3.setMarks(5);
        q3.setOrderIndex(3);
        q3.setExplanation("Binary Heap, Fibonacci Heap, and balanced BSTs can all implement priority queues.");
        q3 = questionRepository.save(q3);

        QuestionOption q3Opt1 = optionRepository.save(new QuestionOption(q3, "Binary Heap", true, 1));
        QuestionOption q3Opt2 = optionRepository.save(new QuestionOption(q3, "Fibonacci Heap", true, 2));
        QuestionOption q3Opt3 = optionRepository.save(new QuestionOption(q3, "Circular Queue without Priority", false, 3));
        QuestionOption q3Opt4 = optionRepository.save(new QuestionOption(q3, "Self-balancing Binary Search Tree", true, 4));

        // Q4: Short Answer
        QuizQuestion q4 = new QuizQuestion();
        q4.setQuiz(quiz1);
        q4.setQuestionText("Briefly explain the primary difference between a stack and a queue in terms of element access order.");
        q4.setQuestionType(QuestionType.SHORT_ANSWER);
        q4.setMarks(5);
        q4.setOrderIndex(4);
        q4.setExplanation("A stack follows LIFO (Last In First Out), while a queue follows FIFO (First In First Out).");
        q4 = questionRepository.save(q4);

        // Aarav's Attempt on Quiz 1
        QuizAttempt attempt1 = new QuizAttempt();
        attempt1.setQuiz(quiz1);
        attempt1.setStudent(studentProfile1);
        attempt1.setStartTime(LocalDateTime.now().minusDays(1).minusMinutes(25));
        attempt1.setSubmitTime(LocalDateTime.now().minusDays(1).minusMinutes(7));
        attempt1.setCompleted(true);
        attempt1.setEvaluated(true);
        attempt1.setScore(18.0);
        attempt1.setTeacherFeedback("Superb theoretical clarity and precise answers.");
        attempt1 = attemptRepository.save(attempt1);

        QuizAnswer ans1 = new QuizAnswer();
        ans1.setAttempt(attempt1);
        ans1.setQuestion(q1);
        ans1.setSelectedOptionIds(String.valueOf(q1Correct.getId()));
        ans1.setMarksAwarded(5.0);
        ans1.setEvaluated(true);
        answerRepository.save(ans1);

        QuizAnswer ans2 = new QuizAnswer();
        ans2.setAttempt(attempt1);
        ans2.setQuestion(q2);
        ans2.setSelectedOptionIds(String.valueOf(q2Correct.getId()));
        ans2.setMarksAwarded(5.0);
        ans2.setEvaluated(true);
        answerRepository.save(ans2);

        QuizAnswer ans3 = new QuizAnswer();
        ans3.setAttempt(attempt1);
        ans3.setQuestion(q3);
        ans3.setSelectedOptionIds(q3Opt1.getId() + "," + q3Opt2.getId() + "," + q3Opt4.getId());
        ans3.setMarksAwarded(5.0);
        ans3.setEvaluated(true);
        answerRepository.save(ans3);

        QuizAnswer ans4 = new QuizAnswer();
        ans4.setAttempt(attempt1);
        ans4.setQuestion(q4);
        ans4.setTextAnswer("A stack operates under LIFO (Last-In-First-Out) where push and pop take place at the same end (top). A queue operates under FIFO (First-In-First-Out) where elements are enqueued at the rear and dequeued at the front.");
        ans4.setMarksAwarded(3.0);
        ans4.setEvaluated(true);
        ans4.setComments("Accurate explanation.");
        answerRepository.save(ans4);

        // 12. Practicals / Labs
        Practical practical1 = new Practical();
        practical1.setSubject(dsa);
        practical1.setTeacher(teacher1);
        practical1.setExperimentNumber("EXP-01");
        practical1.setTitle("Breadth-First Search (BFS) and Depth-First Search (DFS) on Directed Graphs");
        practical1.setDescription("Construct adjacency matrix and adjacency list representations for directed graphs. Implement iterative BFS with queue and recursive DFS. Find connected components and shortest cycle.");
        practical1.setVivaQuestions("1. What is the time complexity of BFS using adjacency list vs matrix?\n2. How does DFS detect cycles in a directed graph?\n3. Name a real-world application of topological sort.");
        practical1.setMaxMarks(30);
        practical1.setDueDate(LocalDateTime.now().plusDays(4));
        practical1 = practicalRepository.save(practical1);

        PracticalRubricComponent c1 = rubricRepository.save(new PracticalRubricComponent(practical1, "Experiment Execution & Code Correctness", 10, 1));
        PracticalRubricComponent c2 = rubricRepository.save(new PracticalRubricComponent(practical1, "Output / Test Case Results", 5, 2));
        PracticalRubricComponent c3 = rubricRepository.save(new PracticalRubricComponent(practical1, "Viva Voce Performance", 5, 3));
        PracticalRubricComponent c4 = rubricRepository.save(new PracticalRubricComponent(practical1, "Lab Record & Documentation", 10, 4));

        // Aarav's Practical Submission & Evaluation
        PracticalSubmission pracSub1 = new PracticalSubmission();
        pracSub1.setPractical(practical1);
        pracSub1.setStudent(studentProfile1);
        pracSub1.setSubmissionDate(LocalDateTime.now().minusDays(1));
        pracSub1.setReportFileName("Aarav_Patel_EXP01_Graph_BFS_DFS.pdf");
        pracSub1.setReportFileUrl("/uploads/Aarav_Patel_EXP01_Graph_BFS_DFS.pdf");
        pracSub1.setRemarks("Implemented both Adjacency Matrix and List models. Attached terminal outputs demonstrating shortest path traversal.");
        pracSub1.setStatus("EVALUATED");
        pracSub1.setEvaluatedAt(LocalDateTime.now().minusHours(4));
        pracSub1.setTotalMarksObtained(27.0);
        pracSub1.setTeacherFeedback("Flawless execution. Answered all viva questions on topological sorting and cycle detection with confidence.");
        pracSub1 = practicalSubmissionRepository.save(pracSub1);

        practicalScoreRepository.save(new PracticalComponentScore(pracSub1, c1, 9.5, "Clean recursive DFS"));
        practicalScoreRepository.save(new PracticalComponentScore(pracSub1, c2, 4.5, "All sample graphs traversed"));
        practicalScoreRepository.save(new PracticalComponentScore(pracSub1, c3, 5.0, "Excellent viva response"));
        practicalScoreRepository.save(new PracticalComponentScore(pracSub1, c4, 8.0, "Well-formatted observations"));

        // 13. Assessments & Marks (Dedicated Marks Module)
        Assessment assess1 = assessmentRepository.save(new Assessment(dsa, teacher1, "Assignment 1: AVL Tree", AssessmentType.ASSIGNMENT, 20.0, LocalDate.now().minusDays(1)));
        Assessment assess2 = assessmentRepository.save(new Assessment(dsa, teacher1, "DSA Mid-Term Online Assessment", AssessmentType.QUIZ, 20.0, LocalDate.now().minusDays(1)));
        Assessment assess3 = assessmentRepository.save(new Assessment(dsa, teacher1, "EXP-01: BFS & DFS Graph Lab", AssessmentType.PRACTICAL, 30.0, LocalDate.now().minusDays(1)));
        Assessment assess4 = assessmentRepository.save(new Assessment(dsa, teacher1, "Class Attendance & Participation", AssessmentType.CLASS_PARTICIPATION, 10.0, LocalDate.now()));
        Assessment assess5 = assessmentRepository.save(new Assessment(dbms, teacher2, "DBMS Unit 1 Internal Test", AssessmentType.INTERNAL_TEST, 40.0, LocalDate.now().minusDays(3)));

        // Student Marks for Aarav
        studentMarkRepository.save(new StudentMark(assess1, studentProfile1, 18.5, "Strong algorithmic rigor", teacher1));
        studentMarkRepository.save(new StudentMark(assess2, studentProfile1, 18.0, "Top 5% score in cohort", teacher1));
        studentMarkRepository.save(new StudentMark(assess3, studentProfile1, 27.0, "Clear viva explanation", teacher1));
        studentMarkRepository.save(new StudentMark(assess4, studentProfile1, 9.5, "Active participant in discussions", teacher1));
        studentMarkRepository.save(new StudentMark(assess5, studentProfile1, 36.0, "Strong grasp of relational algebra", teacher2));

        // Student Marks for Priya
        studentMarkRepository.save(new StudentMark(assess1, studentProfile2, 16.0, "Good attempt, minor rotation edge bug", teacher1));
        studentMarkRepository.save(new StudentMark(assess4, studentProfile2, 9.0, "Regular attendance", teacher1));
        studentMarkRepository.save(new StudentMark(assess5, studentProfile2, 34.5, "Solid SQL query formulation", teacher2));

        // Student Marks for Rahul
        studentMarkRepository.save(new StudentMark(assess5, studentProfile3, 31.0, "Fair understanding of normalization", teacher2));

        // 14. Notifications
        notificationRepository.save(new Notification(studentUser1, "Assignment Graded", "Your Assignment 1: AVL Tree has been evaluated. Marks: 18.5 / 20.0", "/student/assignments/" + assign1.getId(), "MARKS"));
        notificationRepository.save(new Notification(studentUser1, "Quiz Result Released", "DSA Mid-Term Online Assessment results have been released. Score: 18.0 / 20.0", "/student/quizzes/" + quiz1.getId() + "/result", "QUIZ"));
        notificationRepository.save(new Notification(studentUser1, "Practical Evaluated", "EXP-01 BFS & DFS Lab record evaluated. Total: 27.0 / 30.0", "/student/practicals/" + practical1.getId(), "PRACTICAL"));

        notificationRepository.save(new Notification(teacher1, "New Submission Received", "Priya Sharma submitted Assignment 1: AVL Tree.", "/teacher/assignments/" + assign1.getId() + "/submissions", "SUBMISSION"));
        notificationRepository.save(new Notification(teacher2, "New Lab Submission", "Rahul Verma submitted Assignment 2: Schema Normalization.", "/teacher/assignments/" + assign2.getId() + "/submissions", "SUBMISSION"));

        notificationRepository.save(new Notification(admin, "System Initialization", "College LMS database and initial academic records loaded successfully.", "/admin/dashboard", "GENERAL"));

        System.out.println("College LMS Demo Data Initialized Successfully!");
    }
}
