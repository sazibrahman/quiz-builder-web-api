package com.sazibrahman.quizservice.service;

import java.util.UUID;

import org.springframework.security.core.AuthenticationException;

import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.PublishQuizRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;

public interface QuizService {

    void throwExceptionIfNotCreatedByLoggedInUser(User loggedUser, Quiz existQuiz) throws AuthenticationException;

    Quiz getQuizIfDeletable(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
    Quiz getQuizIfEditable(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    Quiz getQuizIfPublishable(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    Quiz getQuizIfAttemptable1(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException, InvalidInputException;

    
    
//    Quiz getQuizIfNotDeletedAndNotPublished(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
//    Quiz getQuizIfNotDeleted(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
//    Quiz getQuizIfAttemptable(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
    
    Quiz createQuiz(User loggedUser, CreateQuizRequest req) throws InvalidInputException;
    Quiz editQuiz(User loggedUser, UUID quizUuid, EditQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    void deleteQuiz(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
    Quiz publishQuiz(User loggedUser, UUID quizUuid, PublishQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    
}
