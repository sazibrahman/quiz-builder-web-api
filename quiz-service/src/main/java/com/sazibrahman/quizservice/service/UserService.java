package com.sazibrahman.quizservice.service;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SignupUserRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;

public interface UserService {

    User signupUser(SignupUserRequest req) throws InvalidInputException;

}
