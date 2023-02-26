package com.sazibrahman.quizservice.service;

import java.util.UUID;

import org.springframework.security.core.AuthenticationException;

import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionAttemptRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;

public interface QuestionAttemptService {

    // void throwExceptionIfNotCreatedByLoggedInUser(User loggedUser, Quiz existQuiz) throws AuthenticationException;
    
    // Quiz getQuizIfNotDeletedAndNotPublished(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    // Quiz getQuizIfNotDeleted(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
    
	QuestionAttempt createQuestionAttempt(User loggedUser, UUID questionUuid, CreateQuestionAttemptRequest req) throws InvalidInputException, AuthenticationException, NotFoundException;

	
	// QuizAttempt editQuizAttempt(User loggedUser, UUID quizUuid, EditQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    // void deleteQuizAttempt(User loggedUser, UUID quizUuid) throws AuthenticationException;
    // QuizAttempt submitQuiz(User loggedUser, UUID quizUuid, PublishQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    
}
