package com.sazibrahman.quizservice.web.security.jwt.model;

import java.io.Serializable;

public class JwtTokenRefreshResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    public JwtTokenRefreshResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
