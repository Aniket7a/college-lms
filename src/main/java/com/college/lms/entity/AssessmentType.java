package com.college.lms.entity;

public enum AssessmentType {
    ASSIGNMENT,
    QUIZ,
    PRACTICAL,
    INTERNAL_TEST,
    VIVA,
    CLASS_PARTICIPATION;

    public String getDisplayName() {
        switch (this) {
            case ASSIGNMENT: return "Assignment";
            case QUIZ: return "Quiz";
            case PRACTICAL: return "Practical / Lab";
            case INTERNAL_TEST: return "Internal Test";
            case VIVA: return "Viva Voce";
            case CLASS_PARTICIPATION: return "Class Participation";
            default: return name();
        }
    }
}
