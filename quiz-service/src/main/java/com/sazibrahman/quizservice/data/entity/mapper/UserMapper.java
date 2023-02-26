package com.sazibrahman.quizservice.data.entity.mapper;

import java.time.ZonedDateTime;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SignupUserRequest;
import com.sazibrahman.quizservice.data.model.v1.UserModel;

public class UserMapper {
    
	private UserMapper() {}

    public static User prepareEntityToInsert(SignupUserRequest req, String encryptedPassword) {
        User entity = new User();
        
        entity.setEmail(req.getEmail());
        entity.setPassword(encryptedPassword);
        
        entity.setFirstName(req.getFirstName());
        entity.setMiddleName(req.getMiddleName());
        entity.setLastName(req.getLastName());
        
        entity.setCreatedDate(ZonedDateTime.now());
        
        return entity;
    }
    
    public static UserModel entityToDomain(User entity) {
        if (entity == null) {
            return null;
        }
        
        UserModel domain = new UserModel();

        domain.setUuid(entity.getUuid());
        domain.setEmail(entity.getEmail());
        
        domain.setFirstName(entity.getFirstName());
        domain.setMiddleName(entity.getMiddleName());
        domain.setLastName(entity.getLastName());

        return domain;
    }
    
}