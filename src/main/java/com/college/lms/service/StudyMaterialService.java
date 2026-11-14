package com.college.lms.service;

import com.college.lms.entity.Enrollment;
import com.college.lms.entity.StudyMaterial;
import com.college.lms.entity.Subject;
import com.college.lms.entity.User;
import com.college.lms.repository.StudyMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final NotificationService notificationService;

    public StudyMaterialService(StudyMaterialRepository studyMaterialRepository,
                                NotificationService notificationService) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.notificationService = notificationService;
    }

    public List<StudyMaterial> getMaterialsBySubject(Subject subject) {
        return studyMaterialRepository.findBySubjectOrderByUploadDateDesc(subject);
    }

    public List<StudyMaterial> getMaterialsByTeacher(User teacher) {
        return studyMaterialRepository.findByTeacherOrderByUploadDateDesc(teacher);
    }

    public Optional<StudyMaterial> getMaterialById(Long id) {
        return studyMaterialRepository.findById(id);
    }

    @Transactional
    public StudyMaterial saveMaterial(StudyMaterial material) {
        boolean isNew = (material.getId() == null);
        StudyMaterial saved = studyMaterialRepository.save(material);

        if (isNew && saved.getSubject() != null) {
            for (Enrollment e : saved.getSubject().getEnrollments()) {
                notificationService.createNotification(
                    e.getStudent().getUser(),
                    "New Study Material: " + saved.getTitle(),
                    "New study material has been uploaded in " + saved.getSubject().getName() + " (" + saved.getMaterialType() + ").",
                    "/student/materials",
                    "MATERIAL"
                );
            }
        }

        return saved;
    }

    @Transactional
    public void deleteMaterial(Long id) {
        studyMaterialRepository.deleteById(id);
    }
}
