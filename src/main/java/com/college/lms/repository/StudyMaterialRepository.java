package com.college.lms.repository;

import com.college.lms.entity.StudyMaterial;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findBySubjectOrderByUploadDateDesc(Subject subject);
    List<StudyMaterial> findByTeacherOrderByUploadDateDesc(User teacher);
    List<StudyMaterial> findBySubjectAndMaterialType(Subject subject, String materialType);
}
