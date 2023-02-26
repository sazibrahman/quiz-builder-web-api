package com.sazibrahman.quizservice.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.mapper.QuestionAttemptMapper;
import com.sazibrahman.quizservice.data.entity.v1.Answer;
import com.sazibrahman.quizservice.data.entity.v1.AnswerAttempt;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;
import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionAttemptRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.repositiry.AnswerRepository;
import com.sazibrahman.quizservice.repositiry.QuestionAttemptRepository;
import com.sazibrahman.quizservice.repositiry.QuizAttemptRepository;
import com.sazibrahman.quizservice.service.QuestionAttemptService;
import com.sazibrahman.quizservice.service.QuestionService;
import com.sazibrahman.quizservice.service.QuizAttemptService;

@Service
public class QuestionAttemptServiceImpl implements QuestionAttemptService {

    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Autowired
    private QuestionAttemptRepository questionAttemptRepository;
    
    @Autowired
    private AnswerRepository answerRepository;
    
	@Override
	public QuestionAttempt createQuestionAttempt(User loggedUser, UUID questionUuid, CreateQuestionAttemptRequest req) throws InvalidInputException, AuthenticationException, NotFoundException {
		if(! req.isSkipped()) {
			if(CollectionUtils.isEmpty(req.getSelectedAnswerUuids())) {
				throw new InvalidInputException("Answer is required");
			}
		}
		
		Question existQuestion = questionService.getQuestionIfAttemptable1(loggedUser, questionUuid);
		
        QuizAttempt existQuizAttempt = quizAttemptRepository.findByQuizAndAttemptedBy(existQuestion.getQuiz(), loggedUser);
        if(existQuizAttempt != null) {
            if(existQuizAttempt.getEndDate() != null) {
            	throw new InvalidInputException("Already completed");
            }
        }
        
        double score = 0;
        List<AnswerAttempt> selectedAnswerAttempts = new ArrayList<>();

        if(req.isSkipped()) {
            /**
             * skipped
             */
        	score = 0;
            selectedAnswerAttempts = new ArrayList<>();
        } else {
	        if(CollectionUtils.isEmpty(req.getSelectedAnswerUuids())) {
	        	throw new InvalidInputException("No answer selected");
	        }
        	
        	if(existQuestion.getQuestionType().equals(QuestionType.SINGLE_CORRECT)) {
	        	if(req.getSelectedAnswerUuids().size() != 1) {
	        		throw new InvalidInputException("Single select question can not have multiple correct answers");
	        	}
	        }
        	
        	/**
	         * validate selected answers
	         */
	        for(UUID selectedAnswerUuid : req.getSelectedAnswerUuids()) {
	        	Answer selectedAnswer = answerRepository.findByUuid(selectedAnswerUuid);
	        	if(selectedAnswer == null) {
	        		throw new NotFoundException("Answer not found");
	        	}
	        	
	        	if(! selectedAnswer.getQuestion().equals(existQuestion)) {
	        		throw new InvalidInputException("Invalid answer attempt");
	        	}
	        }
	        
	        Pair<Double, List<AnswerAttempt>> res = calculateScoreForQuestion(existQuestion, req.getSelectedAnswerUuids());
	        score = res.getLeft();
	        selectedAnswerAttempts = res.getRight();
        }
        
        QuestionAttempt newQuestionAttempt = QuestionAttemptMapper.prepareEntityToInsert(req.isSkipped(), score, (double) score * 100);
        newQuestionAttempt.setQuestion(existQuestion);
        newQuestionAttempt.setQuizAttempt(existQuizAttempt);
        
        newQuestionAttempt.setAnswerAttempts(selectedAnswerAttempts);
        for(AnswerAttempt aa : selectedAnswerAttempts) {
        	aa.setQuestionAttempt(newQuestionAttempt);
        }
        
        newQuestionAttempt = questionAttemptRepository.saveAndFlush(newQuestionAttempt);

        /**
         * if all questions attempted, close QuizAttempt
         */
        Page<Question> unattemptedQuestions = quizAttemptService.getNextUnattemptedQuestions(loggedUser, existQuizAttempt, 1);
        if(unattemptedQuestions.getContent().size() <= 0) {
        	
        	existQuizAttempt.setEndDate(ZonedDateTime.now());
        	
        	Pair<Double, Double> res = calculateScoreForQuizAttempt(existQuizAttempt);
        	existQuizAttempt.setScore(res.getLeft());
        	existQuizAttempt.setCorrectPercent(res.getRight());
        	
        	quizAttemptRepository.saveAndFlush(existQuizAttempt);
        }
        
        return newQuestionAttempt;
	}
    
	/**
	 * Triple<Score, CorrectPercent, List<SelectedAnswer>>
	 */
    private Pair<Double, List<AnswerAttempt>> calculateScoreForQuestion(Question question, List<UUID> selectedAnswerUuids) {
        int masterCorrectCount = 0;
        int masterIncorrectCount = 0;
        
        int userCorrectCount = 0;
        int userIncorrectCount = 0;
        
        List<AnswerAttempt> selectedAnswerAttempts = new ArrayList<>();
        for(Answer masterAnswer : question.getAnswers()) {
        	if(masterAnswer.isCorrect()) {
        		masterCorrectCount++;
        	} else {
        		masterIncorrectCount++;
        	}
        	
        	if(selectedAnswerUuids.contains(masterAnswer.getUuid())) {
        		if(masterAnswer.isCorrect()) {
    				// user gave correct answer
        			userCorrectCount++;
        		} else {
    				// user gave incorrect answer
        			userIncorrectCount++;
        		}
        		
                AnswerAttempt answerAttempt = new AnswerAttempt();
                answerAttempt.setAnswer(masterAnswer);

        		selectedAnswerAttempts.add(answerAttempt);
        		
        	} else {
				// user skipped answer
        		// do nothing
        	}
        }
        
        double weightCorrect = (double) 1 / masterCorrectCount;
        double weightIncorrect = (double) 1 / masterIncorrectCount;
        
        double score = (double) (userCorrectCount * weightCorrect) - (userIncorrectCount * weightIncorrect);
        
        return Pair.of(score, selectedAnswerAttempts);
    }
    
    private Pair<Double, Double> calculateScoreForQuizAttempt(QuizAttempt quizAttempt) {
    	Quiz quiz = quizAttempt.getQuiz();
    	
    	Map<Question, QuestionAttempt> quesAttmpMap = new HashMap<>();
    	Set<Answer> ansSelectedMap = new HashSet<>();
    	for(QuestionAttempt qa : quizAttempt.getQuestionAttempts()) {
    		quesAttmpMap.put(qa.getQuestion(), qa);
    		
    		for(AnswerAttempt aa : qa.getAnswerAttempts()) {
    			ansSelectedMap.add(aa.getAnswer());
    		}
    	}
    	
        double sumScore = 0;
        double sumCorrectPercent = 0;
    	
        for(Question q : quiz.getQuestions()) {
        	QuestionAttempt qa = quesAttmpMap.get(q);
        	if(qa != null) {
        		sumScore += qa.getScore();
        		sumCorrectPercent += qa.getCorrectPercent();
        	}
        }
        
//        quizAttempt.setScore(sumScore);
//        quizAttempt.setCorrectPercent((double) sumCorrectPercent / quiz.getQuestions().size());
        
        return Pair.of(sumScore, (double) sumCorrectPercent / quiz.getQuestions().size());
    }

}
