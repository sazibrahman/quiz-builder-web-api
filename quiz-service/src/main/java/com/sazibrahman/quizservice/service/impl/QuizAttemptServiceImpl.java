package com.sazibrahman.quizservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.mapper.QuizAttemptMapper;
import com.sazibrahman.quizservice.data.entity.mapper.QuizSessionLiveMapper;
import com.sazibrahman.quizservice.data.entity.mapper.QuizSessionRecordForParticipantMapper;
import com.sazibrahman.quizservice.data.entity.mapper.QuizSessionRecordForPublisherMapper;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionLive;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForPublisher;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.repositiry.QuestionRepository;
import com.sazibrahman.quizservice.repositiry.QuizAttemptRepository;
import com.sazibrahman.quizservice.service.QuizAttemptService;
import com.sazibrahman.quizservice.service.QuizService;

@Service
public class QuizAttemptServiceImpl implements QuizAttemptService {

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

	@Override
	public QuizAttempt createQuizAttempt(User loggedUser, UUID quizUuid) throws InvalidInputException, AuthenticationException, NotFoundException {
        Quiz existQuiz = quizService.getQuizIfAttemptable1(loggedUser, quizUuid);
        
        QuizAttempt existQuizAttempt = quizAttemptRepository.findByQuizAndAttemptedBy(existQuiz, loggedUser);
        if(existQuizAttempt != null) {
            if(existQuizAttempt.getEndDate() != null) {
            	throw new InvalidInputException("Already attempted, but not completed");
            } else {
            	throw new InvalidInputException("Already completed");
            }
        }
        
        QuizAttempt newQuizAttempt = QuizAttemptMapper.prepareEntityToInsert();
        newQuizAttempt.setQuiz(existQuiz);
        newQuizAttempt.setAttemptedBy(loggedUser);
        
        return quizAttemptRepository.saveAndFlush(newQuizAttempt);
	}
    
	@Override
	public Page<Question> getNextUnattemptedQuestions(User loggedUser, QuizAttempt existQuizAttempt, int pageSize) throws AuthenticationException {
        if(! existQuizAttempt.getAttemptedBy().equals(loggedUser)) {
        	throw new InsufficientAuthenticationException("Unauthorized");
        }
        
		List<UUID> attemptedQuestionUuids = existQuizAttempt.getQuestionAttempts().stream().map(qa -> qa.getQuestion().getUuid()).collect(Collectors.toList());
        
        Page<Question> unattemptedQuestions = null;
        if(CollectionUtils.isEmpty(attemptedQuestionUuids)) {
        	unattemptedQuestions = questionRepository.findAllByQuizOrderByQuestionText(existQuizAttempt.getQuiz(), PageRequest.of(0, pageSize));
        } else {
        	unattemptedQuestions = questionRepository.findAllByQuizAndUuidNotInOrderByQuestionText(existQuizAttempt.getQuiz(), attemptedQuestionUuids, PageRequest.of(0, pageSize));
        }
        
        return unattemptedQuestions;
	}
	
	@Override
	public QuizSessionLive getNextQuestions(User loggedUser, UUID quizAttemptUuid, int pageSize) throws NotFoundException, AuthenticationException {
        QuizAttempt existQuizAttempt = quizAttemptRepository.findByUuid(quizAttemptUuid);
        if(existQuizAttempt == null) {
        	throw new NotFoundException("QuizAttempt not found");
        }
        
        return getNextQuestions(loggedUser, existQuizAttempt, pageSize);
	}
	
	private QuizSessionLive getNextQuestions(User loggedUser, QuizAttempt existQuizAttempt, int pageSize) throws NotFoundException, AuthenticationException {
        Page<Question> unattemptedQuestions = getNextUnattemptedQuestions(loggedUser, existQuizAttempt, pageSize);
        
        QuizSessionLive quizSession = QuizSessionLiveMapper.entityToDomain(unattemptedQuestions);
        return quizSession;
	}
	
	@Override
	public QuizSessionRecordForPublisher getQuizSessionRecordForPublisher(User loggedUser, UUID quizAttemptUuid) throws NotFoundException, AuthenticationException {
        QuizAttempt existQuizAttempt = quizAttemptRepository.findByUuid(quizAttemptUuid);
        if(existQuizAttempt == null) {
        	throw new NotFoundException("QuizAttempt not found");
        }
        
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuizAttempt.getQuiz());
        
        return QuizSessionRecordForPublisherMapper.entityToDomain(existQuizAttempt);
	}
	
	@Override
	public QuizSessionRecordForParticipant getQuizSessionRecordForParticipant(User loggedUser, UUID quizAttemptUuid) throws NotFoundException, AuthenticationException {
        QuizAttempt existQuizAttempt = quizAttemptRepository.findByUuid(quizAttemptUuid);
        if(existQuizAttempt == null) {
        	throw new NotFoundException("QuizAttempt not found");
        }
        
        if(! existQuizAttempt.getAttemptedBy().equals(loggedUser)) {
        	throw new InsufficientAuthenticationException("Unauthorized");
        }
        
        return QuizSessionRecordForParticipantMapper.entityToDomain(existQuizAttempt);
	}
	
}
