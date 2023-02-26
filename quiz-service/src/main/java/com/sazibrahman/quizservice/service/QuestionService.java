package com.sazibrahman.quizservice.service;

import java.util.UUID;

import org.springframework.security.core.AuthenticationException;

import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuestionRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;

public interface QuestionService {

    Question getQuestionIfDeletable(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException;
    Question getQuestionIfEditable(User loggedUser, UUID questionUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    Question getQuestionIfPublishable(User loggedUser, UUID questionUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    Question getQuestionIfAttemptable1(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException, InvalidInputException;
    
    

    Question createQuestion(User loggedUser, UUID quizUuid, CreateQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    Question editQuestion(User loggedUser, UUID questionUuid, EditQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    void deleteQuestion(User loggedUser, UUID answerUuid) throws AuthenticationException, InvalidInputException, NotFoundException;

}
