package com.college.lms.repository;

import com.college.lms.entity.MarksAuditLog;
import com.college.lms.entity.StudentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarksAuditLogRepository extends JpaRepository<MarksAuditLog, Long> {
    List<MarksAuditLog> findByStudentMarkOrderByUpdatedAtDesc(StudentMark studentMark);
}
