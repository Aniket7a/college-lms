# College Learning Management System (LMS)

A modern, responsive, enterprise-grade College Learning Management System built with **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, and **Chart.js**.

Designed with inspiration from Linear, Notion, and Coursera, the LMS provides role-based interfaces for **College Administrators**, **Faculty Members (Teachers)**, and **Students**, enforcing strict role-based access control, server-validated workflows, real-time quizzes, laboratory practical rubrics, and an audited marks management system.

---

## 🌟 Demo Credentials & Quick Login

The application comes pre-loaded with comprehensive demo datasets (departments, courses, subjects, assignments, quizzes, practicals, student marks, and notifications). A convenient 1-click credential selector is available on the login page:

| Role | Email / College ID | Password | Representative Persona |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@college.edu` | `admin123` | Dean Robert Harrison |
| **Teacher** | `dr.sharma@college.edu` | `teacher123` | Dr. Rajesh Sharma (HOD & Professor, CSE) |
| **Teacher** | `prof.smith@college.edu` | `teacher123` | Prof. Emily Smith (Associate Professor, CSE) |
| **Student** | `student1@college.edu` | `student123` | Aarav Patel (Roll: `2024-CS-001`, Sem 3) |
| **Student** | `student2@college.edu` | `student123` | Priya Sharma (Roll: `2024-CS-002`, Sem 3) |
| **Student** | `student3@college.edu` | `student123` | Rahul Verma (Roll: `2024-IT-001`, Sem 3) |

---

## 🏗️ Architecture & Technology Stack

- **Backend**: Java 17, Spring Boot 3.4.0, Spring Security 6, Spring Data JPA, Hibernate ORM
- **Database**:
  - **Embedded / File Mode (Default)**: Zero-configuration persistent H2 database (`./data/collegelms`)
  - **MySQL Production Mode**: Fully supported via `--spring.profiles.active=mysql` connecting to `jdbc:mysql://localhost:3306/college_lms`
- **Frontend**: Thymeleaf template engine, HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3.2, Bootstrap Icons, Chart.js 4.4.1
- **Security & Authorization**: Session-based authentication, BCrypt password encryption, URL authorization filters, custom role-redirection handler (`CustomAuthenticationSuccessHandler`), cross-site request forgery (CSRF) protection.
- **Design System**: Deep navy primary (`#0F172A`), soft blue accents (`#2563EB`), crisp border tokens (`#E2E8F0`), card elevation, custom responsive sidebar navigation with mobile drawer support.

---

## 🚀 Key Modules & Functional Workflows

### 1. Authentication & Role-Based Authorization
- **Common Sign-In Portal**: College branding, remember-me support, 1-click test credentials autofill.
- **Role Redirection**:
  - `ROLE_ADMIN` $\rightarrow$ `/admin/dashboard`
  - `ROLE_TEACHER` $\rightarrow$ `/teacher/dashboard`
  - `ROLE_STUDENT` $\rightarrow$ `/student/dashboard`
- **Route Protection**: Strictly blocks unauthorized access (e.g., student attempting to access `/admin/**` or `/teacher/**` returns `403 Forbidden`).

---

### 2. Administrator Module
- **Dashboard & KPIs**: Total student registrations, faculty headcount, active courses, total subjects, active assignments, and submission count.
- **Visual Analytics**: Interactive Chart.js charts showing enrollment breakdown by subject and department distribution.
- **Student Management**: Register new students, assign roll numbers, department, degree program, and semester. View student profile and academic transcripts. Toggle active/inactive status.
- **Faculty Management**: Register teachers, assign employee IDs, designations, qualifications, and specializations.
- **Courses & Subjects**: Create degree programs, configure semesters and credits, and assign faculty instructors to subjects.
- **Enrollment Management**: Manage curriculum enrollments for each semester.
- **Reports**: Institution-wide pass percentage charts, assignment completion tracking, and faculty teaching load reports.

---

### 3. Teacher Module
- **Faculty Dashboard**: Assigned subject counts, enrolled students, pending evaluations counter, active assignment trackers, and recent submissions table.
- **Subject Management**: Dedicated subject overview tabs displaying lecture notes, assignments, quizzes, practicals, and enrolled student rosters.
- **Assignment Management**:
  - Create assignments with start dates, due dates, maximum marks, submission types (File Upload, Text Answer, or Both), and attached reference PDFs.
  - Review submitted student work, view text answers or download files.
  - Grade submissions with numerical marks (validated $\le$ maximum marks) and feedback comments.
  - Enable/disable resubmissions for individual students.
- **Quiz Management**:
  - Create timed online tests with duration in minutes, total marks, and passing thresholds.
  - Question bank supporting **MCQ (Single choice)**, **Multiple Select (Checkboxes)**, **True/False**, and **Short Answer / Subjective**.
  - Monitor student attempts, auto-evaluated MCQ scores, and manual subjective question grading.
  - Toggle result release to control when students can view scores and question explanations.
- **Practical & Laboratory Management**:
  - Create laboratory experiments with experiment numbers (e.g. `EXP-01`), aims, procedures, lab manual PDF uploads, and viva questions.
  - Configurable 4-component rubric: *Experiment Execution*, *Output Verification*, *Viva Voce*, and *Lab Record*.
  - Grade lab reports with component-level rubric scores and observations.
- **Dedicated Marks & Evaluation Sheet**:
  - Filter by subject and assessment category (Assignment, Quiz, Practical, Internal Test, Viva, Class Participation).
  - Live numerical validation (ensuring marks $\ge 0$ and $\le$ max marks).
  - Automatic audit trail (`MarksAuditLog`) logging previous marks, new marks, timestamp, modifying teacher, and update reason.
- **Study Materials**: Upload lecture notes, slide decks, syllabus blueprints, and reference PDFs with automatic file size computation.

---

### 4. Student Module
- **Student Dashboard**: Enrolled courses, pending assignment countdowns, upcoming quizzes, completed work, and recent grades.
- **Academic Mastery Radar**: Chart.js visualization of student scores compared with class averages.
- **My Subjects**: Access subject details, syllabus, notes download, and instructor information.
- **Assignments**: View instructions, submit text solutions or upload documents (`.pdf`, `.zip`, `.docx`), track submission status (`Submitted`, `Late`, `Evaluated`), and view teacher feedback.
- **Online Quizzes**:
  - Real-time exam interface with a JavaScript and server-validated countdown timer.
  - Question jump list to navigate across questions.
  - Automatic submission when the timer expires.
  - Instant score calculation and question review (upon release).
- **Practicals**: Download lab manuals, prepare for viva questions, submit lab reports, and inspect component rubric marks.
- **Academic Results & Transcript (`/student/results`)**:
  - Itemized marks table: *Assessment*, *Maximum Marks*, *Obtained Marks*, *Percentage*, *Status*, and *Teacher Remarks*.
  - Cumulative percentage, letter grade (`A+`, `A`, `B`, etc.), and assessment weightage charts.
  - Strict privacy: Students can only view their own performance.

---

### 5. Notification Center
- Top navigation bar bell dropdown with real-time unread badges.
- Dedicated `/notifications` page with mark-all-as-read capabilities.
- Automated triggers: New assignments, published quiz results, graded submissions, and new study materials.

---

## 💻 Running the Application

### Option A: Standard Run (Embedded H2 File Mode - Recommended for Instant Evaluation)
No MySQL installation required. The database files will be initialized automatically in `./data/`:

```bash
# In the project directory:
mvn spring-boot:run
```
Or directly using the bundled Maven wrapper:
```powershell
& "C:\Users\beria\.gemini\antigravity\scratch\maven\bin\mvn.cmd" spring-boot:run
```

Once started, navigate to:
**`http://localhost:8080/login`**

---

### Option B: Running with MySQL
1. Create a MySQL database:
   ```sql
   CREATE DATABASE college_lms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Adjust `src/main/resources/application-mysql.properties` if your MySQL username or password differ.
3. Start the application with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 🗄️ Database Schema & Entities

```
User (id, email, password, fullName, phone, role, active, createdAt)
 ├── TeacherProfile (id, user_id, employeeId, designation, qualification, specialization, department_id)
 └── StudentProfile (id, user_id, rollNumber, department_id, course_id, currentSemester, academicYear)

Department (id, code, name, description)
 └── Course (id, code, name, durationYears, totalSemesters, department_id)
      └── Subject (id, code, name, semester, credits, department_id, course_id, teacher_id)
           ├── Enrollment (id, student_id, subject_id, enrollmentDate, status)
           ├── StudyMaterial (id, subject_id, teacher_id, title, fileUrl, fileSize, materialType)
           ├── Assignment (id, subject_id, teacher_id, title, maxMarks, dueDate, submissionType)
           │    └── AssignmentSubmission (id, assignment_id, student_id, marksObtained, feedback, status)
           ├── Quiz (id, subject_id, teacher_id, title, durationMinutes, totalMarks, resultsReleased)
           │    ├── QuizQuestion (id, quiz_id, questionText, questionType, marks)
           │    │    └── QuestionOption (id, question_id, optionText, isCorrect)
           │    └── QuizAttempt (id, quiz_id, student_id, score, isCompleted, evaluated)
           │         └── QuizAnswer (id, attempt_id, question_id, selectedOptionIds, textAnswer, marksAwarded)
           ├── Practical (id, subject_id, teacher_id, experimentNumber, title, maxMarks, dueDate)
           │    ├── PracticalRubricComponent (id, practical_id, componentName, maxMarks)
           │    └── PracticalSubmission (id, practical_id, student_id, reportFileUrl, totalMarksObtained)
           │         └── PracticalComponentScore (id, submission_id, component_id, marksObtained)
           └── Assessment (id, subject_id, teacher_id, title, assessmentType, maxMarks)
                └── StudentMark (id, assessment_id, student_id, marksObtained, teacherRemarks)
                     └── MarksAuditLog (id, student_mark_id, previousMarks, newMarks, updatedBy, updateReason)

Notification (id, recipient_id, title, message, linkUrl, isRead, type, createdAt)
```
