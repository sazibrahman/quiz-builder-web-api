package com.sazibrahman.quizservice.web.security.jwt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    public static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh}")
    private Long refresh;

    public String getUsernameFromToken(String token) {
        String username = null;
        
        if (null != token && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        final Claims claims = getClaimsFromToken(token);
        if (null != claims) {
            username = claims.getSubject();
        }

        return username;
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put(CLAIM_KEY_USERNAME, username);
        
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, JwtUser user) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(user.getUsername()) 
        );
    }
}
