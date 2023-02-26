package com.sazibrahman.quizservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.AuthenticationException;

import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionLive;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForPublisher;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;

public interface QuizAttemptService {

    // void throwExceptionIfNotCreatedByLoggedInUser(User loggedUser, Quiz existQuiz) throws AuthenticationException;
    
    // Quiz getQuizIfNotDeletedAndNotPublished(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;
    // Quiz getQuizIfNotDeleted(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException;
	
	// QuizAttempt getQuizAttemptIfAttemptable1(User loggedUser, UUID quizAttemptUuid) throws AuthenticationException, NotFoundException, InvalidInputException;
	
	QuizAttempt createQuizAttempt(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException;

	Page<Question> getNextUnattemptedQuestions(User loggedUser, QuizAttempt existQuizAttempt, int pageSize) throws AuthenticationException;
	QuizSessionLive getNextQuestions(User loggedUser, UUID quizAttemptUuid, int pageSize) throws NotFoundException, AuthenticationException;

	QuizSessionRecordForPublisher getQuizSessionRecordForPublisher(User loggedUser, UUID quizAttemptUuid) throws NotFoundException, AuthenticationException;
	QuizSessionRecordForParticipant getQuizSessionRecordForParticipant(User loggedUser, UUID quizAttemptUuid) throws NotFoundException, AuthenticationException;
	
	// QuizAttempt editQuizAttempt(User loggedUser, UUID quizUuid, EditQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    // void deleteQuizAttempt(User loggedUser, UUID quizUuid) throws AuthenticationException;
    // QuizAttempt submitQuiz(User loggedUser, UUID quizUuid, PublishQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException;
    
}
