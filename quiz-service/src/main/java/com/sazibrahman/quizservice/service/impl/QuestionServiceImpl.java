package com.sazibrahman.quizservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.mapper.AnswerMapper;
import com.sazibrahman.quizservice.data.entity.mapper.QuestionMapper;
import com.sazibrahman.quizservice.data.entity.v1.Answer;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest.CreateAnswerRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuestionRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.repositiry.AnswerRepository;
import com.sazibrahman.quizservice.repositiry.QuestionRepository;
import com.sazibrahman.quizservice.service.QuestionService;
import com.sazibrahman.quizservice.service.QuizService;

@Service
public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private AnswerRepository answerRepository;
    
    @Autowired
    private QuizService quizService;

    @Override
    public Question getQuestionIfDeletable(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException {
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new NotFoundException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted
         */
        Quiz existQuiz = quizService.getQuizIfDeletable(loggedUser, existQuestion.getQuiz().getUuid());
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        return existQuestion;
    }
    
    @Override
    public Question getQuestionIfEditable(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException, InvalidInputException {
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new NotFoundException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted
         */
        Quiz existQuiz = quizService.getQuizIfEditable(loggedUser, existQuestion.getQuiz().getUuid());
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        return existQuestion;
    }
    
    @Override
    public Question getQuestionIfPublishable(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException, InvalidInputException {
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new NotFoundException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted
         */
        Quiz existQuiz = quizService.getQuizIfPublishable(loggedUser, existQuestion.getQuiz().getUuid());
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        return existQuestion;
    }
    
    @Override
    public Question getQuestionIfAttemptable1(User loggedUser, UUID questionUuid) throws AuthenticationException, NotFoundException, InvalidInputException {
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new NotFoundException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted
         */
        quizService.getQuizIfAttemptable1(loggedUser, existQuestion.getQuiz().getUuid());
        // quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        return existQuestion;
    }
    
    @Override
    public Question createQuestion(User loggedUser, UUID quizUuid, CreateQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        if (StringUtils.isEmpty(req.getQuestionText())) {
            throw new InvalidInputException("Question text is required");
        }
        
        throwExceptionIfAnswersNotValid(req.getQuestionType(), req.getAnswerRequests());

        Quiz existQuiz = quizService.getQuizIfEditable(loggedUser, quizUuid);
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if(existQuiz.getQuestions().size() > 10) {
            throw new InvalidInputException("Maximum 10 questions allowed");
        }

        Question newQuesEntity = QuestionMapper.prepareEntityToInsert(req);
        addAnswers(newQuesEntity, req.getAnswerRequests());
        
        newQuesEntity.setQuiz(existQuiz);
        existQuiz.getQuestions().add(newQuesEntity);
        
        return questionRepository.saveAndFlush(newQuesEntity);
    }

    @Override
    public Question editQuestion(User loggedUser, UUID questionUuid, EditQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        if (StringUtils.isEmpty(req.getQuestionText())) {
            throw new InvalidInputException("Question text is required");
        }
        
        throwExceptionIfAnswersNotValid(req.getQuestionType(), req.getAnswerRequests());
        
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new InvalidInputException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted and not published
         */
        Quiz existQuiz = quizService.getQuizIfEditable(loggedUser, existQuestion.getQuiz().getUuid());
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);
        
        if(existQuiz.getQuestions().size() >= 10) {
            throw new InvalidInputException("Maximum 10 questions allowed");
        }
        
        existQuestion = QuestionMapper.prepareEntityToEdit(req, existQuestion);
        
        /**
         * delete all answers
         */
        List<Answer> oldAnswers = existQuestion.getAnswers().stream().collect(Collectors.toList());
        existQuestion.getAnswers().clear();
        answerRepository.deleteAll(oldAnswers);
        
        /**
         * add fresh answers
         */
        addAnswers(existQuestion, req.getAnswerRequests());
        
        return questionRepository.saveAndFlush(existQuestion);
    }

    @Override
    public void deleteQuestion(User loggedUser, UUID questionUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        Question existQuestion = questionRepository.findByUuid(questionUuid);
        if (existQuestion == null) {
            throw new NotFoundException("Question not found");
        }
        
        /**
         * validate is Quiz is not deleted and not published
         */
        Quiz existQuiz = quizService.getQuizIfEditable(loggedUser, existQuestion.getQuiz().getUuid());
        quizService.throwExceptionIfNotCreatedByLoggedInUser(loggedUser, existQuiz);

        /**
         * delete question
         */
        existQuiz.getQuestions().remove(existQuestion);
        
        questionRepository.delete(existQuestion);
    }
    
    private void addAnswers(Question questionEntity, List<CreateAnswerRequest> ansReqs) {
        List<Answer> newAnsEntities = new ArrayList<>();
        for(CreateAnswerRequest ansReq : ansReqs) {
            Answer newAnsEntity = AnswerMapper.prepareEntityToInsert(ansReq);
            newAnsEntity.setQuestion(questionEntity);
            
            newAnsEntities.add(newAnsEntity);
        }
        
        questionEntity.setAnswers(newAnsEntities);
    }

    private void throwExceptionIfAnswersNotValid(QuestionType questionType, List<CreateAnswerRequest> ansReqs) throws InvalidInputException {
        if (questionType == null) {
            throw new InvalidInputException("Question type is required, allowed values: SINGLE_CORRECT, MULTI_CORRECT");
        }
        
        if(CollectionUtils.isEmpty(ansReqs)) {
            throw new InvalidInputException("Answer(s) is/are required");
        }
        
        if(ansReqs.size() > 5) {
            throw new InvalidInputException("Maximum 5 answers allowed");
        }
        
        long correctCount = ansReqs.stream().filter(ar -> ar.isCorrect()).count();
        
        if(QuestionType.SINGLE_CORRECT.equals(questionType)) {
            if(correctCount != 1) {
                throw new InvalidInputException("Only one answer must be correct");
            }
        } else if(QuestionType.MULTI_CORRECT.equals(questionType)) {
            if(correctCount <= 1) {
                throw new InvalidInputException("More than one answer must be correct");
            }
        }
    }
    
}
