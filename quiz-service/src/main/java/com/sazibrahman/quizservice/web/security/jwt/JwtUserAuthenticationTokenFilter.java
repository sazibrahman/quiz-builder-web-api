package com.sazibrahman.quizservice.web.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sazibrahman.quizservice.service.impl.JwtUserDetailsService;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

@Component
public class JwtUserAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.type}")
    private String tokenType;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = getToken(request);
        if(! StringUtils.isEmpty(authToken)) {
            handleAuthTokenForUser(authToken, request, response);
        }
        
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String authToken = request.getHeader(this.tokenHeader);
        
        if (authToken != null && authToken.startsWith(tokenType)) {
            return authToken.substring(7);
        }
        
        return null;
    }
    
	private void handleAuthTokenForUser(String authToken, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = jwtTokenUtil.getUsernameFromToken(authToken);

		if (username != null) {
			JwtUser user = this.jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(authToken, user)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				        user, null, user.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
	}

}
