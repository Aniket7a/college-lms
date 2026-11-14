package com.college.lms.config;

import com.college.lms.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN") || role.equals(Role.ROLE_ADMIN.name())) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (role.equals("ROLE_TEACHER") || role.equals(Role.ROLE_TEACHER.name())) {
                response.sendRedirect("/teacher/dashboard");
                return;
            } else if (role.equals("ROLE_STUDENT") || role.equals(Role.ROLE_STUDENT.name())) {
                response.sendRedirect("/student/dashboard");
                return;
            }
        }

        response.sendRedirect("/login?error=true");
    }
}
