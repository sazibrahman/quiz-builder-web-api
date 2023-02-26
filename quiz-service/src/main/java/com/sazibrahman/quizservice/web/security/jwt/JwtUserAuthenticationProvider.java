package com.sazibrahman.quizservice.web.security.jwt;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sazibrahman.quizservice.service.impl.JwtUserDetailsService;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUserAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;
	
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @PostConstruct
    private void init() {
        this.setUserDetailsService(jwtUserDetailsService);
        this.setPasswordEncoder(bCryptPasswordEncoder);
        this.setHideUserNotFoundExceptions(false);
    }
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			Authentication auth = super.authenticate(authentication);

			JwtUser jwtUser = (JwtUser) auth.getPrincipal();
			
			return createSuccessAuthentication(jwtUser, authentication, jwtUser);
			
		} catch (UsernameNotFoundException ex) {
			log.info("UsernameNotFoundException occured", ex.getMessage());
			
			throw ex;
			
		} catch (Exception ex) {
			throw new BadCredentialsException(ex.getMessage());
		}
	}

	@Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}