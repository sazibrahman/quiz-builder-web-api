package com.sazibrahman.quizservice.util;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;


public class UserUtil {

    private UserUtil() {
    }

    public static String getLoginUserName() {
        JwtUser jwtUser = getJwtUser();
        if(jwtUser != null) {
            return jwtUser.getUsername();
        }

        return null;
    }

    public static UUID getLoginUserUuid() {
        JwtUser jwtUser = getJwtUser();
        if(jwtUser != null) {
            return jwtUser.getUuid();
        }

        return null;
    }

    public static JwtUser getJwtUser() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                return (JwtUser) authentication.getPrincipal();
            }
        }

        return null;
    }

}
