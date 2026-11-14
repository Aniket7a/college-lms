package com.college.lms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "practical_rubric_components")
public class PracticalRubricComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practical_id", nullable = false)
    private Practical practical;

    @Column(nullable = false, length = 100)
    private String componentName; // e.g. "Experiment execution", "Output / Result", "Viva", "Lab record"

    private int maxMarks = 10;
    private int orderIndex = 0;

    public PracticalRubricComponent() {}

    public PracticalRubricComponent(Practical practical, String componentName, int maxMarks, int orderIndex) {
        this.practical = practical;
        this.componentName = componentName;
        this.maxMarks = maxMarks;
        this.orderIndex = orderIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Practical getPractical() { return practical; }
    public void setPractical(Practical practical) { this.practical = practical; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
