package com.sazibrahman.quizservice.service.impl;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.mapper.QuizMapper;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.PublishQuizRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.repositiry.QuizRepository;
import com.sazibrahman.quizservice.service.QuizService;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    
    
    
    
    
    
    @Override
    public Quiz getQuizIfDeletable(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException {
        Quiz existQuiz = quizRepository.findByUuid(quizUuid);
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if (existQuiz == null || isDeleted(existQuiz)) {
            throw new NotFoundException("Quiz not found");
        }
        
        return existQuiz;
    }
    
    @Override
    public Quiz getQuizIfEditable(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException {
    	Quiz existQuiz = quizRepository.findByUuid(quizUuid);
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if (existQuiz == null || isDeleted(existQuiz)) {
            throw new NotFoundException("Quiz not found");
        }
        
        if (isPublished(existQuiz)) {
            throw new InvalidInputException("Quiz already published");
        }

        return existQuiz;
    }
    
    @Override
    public Quiz getQuizIfPublishable(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException {
        Quiz existQuiz = quizRepository.findByUuid(quizUuid);
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if (existQuiz == null || isDeleted(existQuiz)) {
            throw new NotFoundException("Quiz not found");
        }
        
        if (isPublished(existQuiz)) {
            throw new InvalidInputException("Quiz already published");
        }

        return existQuiz;
    }
    
    @Override
    public Quiz getQuizIfAttemptable1(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException, InvalidInputException {
        Quiz existQuiz = quizRepository.findByUuid(quizUuid);
        
        if (existQuiz == null || isDeleted(existQuiz)) {
            throw new NotFoundException("Quiz not found (#1)");
        }
        
        if (isNotPublished(existQuiz)) {
            throw new NotFoundException("Quiz not found (#2)");
        }
        
        if(existQuiz.getCreatedBy().equals(loggedUser)) {
        	throw new InvalidInputException("You can't solve your own quiz");
        }

        return existQuiz;
    }
    
    
    @Override
    public void throwExceptionIfNotCreatedByLoggedInUser(User loggedUser, Quiz existQuiz) throws AuthenticationException {
        if(! existQuiz.getCreatedBy().equals(loggedUser)) {
            throw new InsufficientAuthenticationException("Unauthorized");
        }
    }

    @Override
    public Quiz createQuiz(User loggedUser, CreateQuizRequest req) throws InvalidInputException {
        if (StringUtils.isEmpty(req.getTitle())) {
            throw new InvalidInputException("Title is required");
        }

        Quiz newEntity = QuizMapper.prepareEntityToInsert(req);
        
        newEntity.setCreatedBy(loggedUser);
        
        return quizRepository.saveAndFlush(newEntity);
    }

    @Override
    public Quiz editQuiz(User loggedUser, UUID quizUuid, EditQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        if (StringUtils.isEmpty(req.getTitle())) {
            throw new InvalidInputException("Title is required");
        }
        
        Quiz existQuiz = getQuizIfEditable(loggedUser, quizUuid);
        
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        existQuiz.setTitle(req.getTitle());

        return quizRepository.saveAndFlush(existQuiz);
    }
    
    @Override
    public Quiz publishQuiz(User loggedUser, UUID quizUuid, PublishQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        if (req.getStartDate() == null) {
            throw new InvalidInputException("Start date is required");
        }

        Quiz existQuiz = getQuizIfPublishable(loggedUser, quizUuid);
        
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if(existQuiz.getQuestions().size() < 1) {
        	throw new InvalidInputException("No question added to the quiz");
        }
        
        existQuiz.setStartDate(req.getStartDate());
        existQuiz.setEndDate(req.getEndDate());

        return quizRepository.saveAndFlush(existQuiz);
    }

    @Override
    public void deleteQuiz(User loggedUser, UUID quizUuid) throws AuthenticationException, NotFoundException {
    	Quiz existQuiz = getQuizIfDeletable(loggedUser, quizUuid);
    	
        throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        existQuiz.setDeletedDate(ZonedDateTime.now());
        quizRepository.saveAndFlush(existQuiz);
    }

    private boolean isDeleted(Quiz existQuiz) {
        return ! isNotDeleted(existQuiz);
    }
    
    private boolean isNotDeleted(Quiz existQuiz) {
        return existQuiz.getDeletedDate() == null;
    }
    
    private boolean isPublished(Quiz existQuiz) {
        return ! isNotPublished(existQuiz);
    }
    
    private boolean isNotPublished(Quiz existQuiz) {
        return existQuiz.getStartDate() == null;
    }

}
