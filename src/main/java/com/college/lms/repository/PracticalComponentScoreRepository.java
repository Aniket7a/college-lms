package com.college.lms.repository;

import com.college.lms.entity.PracticalComponentScore;
import com.college.lms.entity.PracticalRubricComponent;
import com.college.lms.entity.PracticalSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticalComponentScoreRepository extends JpaRepository<PracticalComponentScore, Long> {
    List<PracticalComponentScore> findBySubmission(PracticalSubmission submission);
    Optional<PracticalComponentScore> findBySubmissionAndComponent(PracticalSubmission submission, PracticalRubricComponent component);
}
