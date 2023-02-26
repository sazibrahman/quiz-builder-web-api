package com.sazibrahman.quizservice.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.mapper.UserMapper;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SignupUserRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.repositiry.UserRepository;
import com.sazibrahman.quizservice.service.UserService;
import com.sazibrahman.quizservice.util.EmailUtil;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Override
    public User signupUser(SignupUserRequest req) throws InvalidInputException {
        if (StringUtils.isEmpty(req.getFirstName())) {
            throw new InvalidInputException("First name is required");
        }
        
        if (StringUtils.isEmpty(req.getLastName())) {
            throw new InvalidInputException("Last name is required");
        }
        
        if (StringUtils.isEmpty(req.getEmail())) {
            throw new InvalidInputException("Email address is required");
        }
        
        if(! EmailUtil.isValid(req.getEmail())) {
            throw new InvalidInputException("Email address is not valid");
        }

        User existUser = userRepository.findByEmail(req.getEmail());
        if (existUser != null) {
            throw new InvalidInputException("Email address already used");
        }
        
        User newEntity = UserMapper.prepareEntityToInsert(req, bCryptPasswordEncoder.encode(req.getPassword()));
        
        return userRepository.saveAndFlush(newEntity);
    }
    
}
