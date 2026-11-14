package com.college.lms.entity;

public enum Role {
    ROLE_ADMIN,
    ROLE_TEACHER,
    ROLE_STUDENT;

    public String getDisplayName() {
        switch (this) {
            case ROLE_ADMIN: return "Administrator";
            case ROLE_TEACHER: return "Teacher";
            case ROLE_STUDENT: return "Student";
            default: return name();
        }
    }
}
