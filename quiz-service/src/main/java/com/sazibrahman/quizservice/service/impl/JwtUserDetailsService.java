package com.sazibrahman.quizservice.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.repositiry.UserRepository;
import com.sazibrahman.quizservice.web.security.jwt.JwtUserFactory;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

@Service("jwtUserDetailsService")
public class JwtUserDetailsService implements UserDetailsService {
	
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public JwtUser loadUserByUsername(String email)  {
        User user =  userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } 
        
        return JwtUserFactory.createFromUser(user, Arrays.asList());
    }
    
}
