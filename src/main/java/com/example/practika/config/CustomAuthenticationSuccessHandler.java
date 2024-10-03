package com.example.practika.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String redirectUrl = "/";

        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_OPERATOR"))) {
            redirectUrl = "/operator/lk"; // URL для операторов
        } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MASTER"))) {
            redirectUrl = "/master/lk"; // URL для мастеров
        } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))){
            redirectUrl = "/user"; // URL для обычных пользователей
        }
        else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"))){
            redirectUrl = "/operator/lk"; // URL для обычных пользователей
        }

        // Перенаправляем пользователя
        response.sendRedirect(redirectUrl);
    }
}
