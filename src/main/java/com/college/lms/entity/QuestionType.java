package com.college.lms.entity;

public enum QuestionType {
    MCQ,
    MULTIPLE_SELECT,
    TRUE_FALSE,
    SHORT_ANSWER;

    public String getDisplayName() {
        switch (this) {
            case MCQ: return "Multiple Choice (Single Answer)";
            case MULTIPLE_SELECT: return "Multiple Select (Checkboxes)";
            case TRUE_FALSE: return "True / False";
            case SHORT_ANSWER: return "Short Answer / Subjective";
            default: return name();
        }
    }
}
