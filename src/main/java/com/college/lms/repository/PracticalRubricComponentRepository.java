package com.college.lms.repository;

import com.college.lms.entity.Practical;
import com.college.lms.entity.PracticalRubricComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticalRubricComponentRepository extends JpaRepository<PracticalRubricComponent, Long> {
    List<PracticalRubricComponent> findByPracticalOrderByOrderIndexAsc(Practical practical);
}
