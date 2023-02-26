package com.sazibrahman.quizservice.web.controller.v1;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.mapper.UserMapper;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SignupUserRequest;
import com.sazibrahman.quizservice.data.model.v1.UserModel;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.service.UserService;

@RestController
public class UserController extends AbstractBaseController {

    @Autowired
    private UserService userService;
    
    @Transactional
    @PostMapping(value = "/sign-up")
    public UserModel signupUser(@RequestBody SignupUserRequest req) throws InvalidInputException {
        User entity = userService.signupUser(req);
        return UserMapper.entityToDomain(entity);
    }
    
}
