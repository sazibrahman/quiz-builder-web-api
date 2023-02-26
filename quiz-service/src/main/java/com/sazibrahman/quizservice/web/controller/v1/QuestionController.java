package com.sazibrahman.quizservice.web.controller.v1;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.mapper.AnswerMapper;
import com.sazibrahman.quizservice.data.entity.mapper.QuestionMapper;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.QuestionDetailModel;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuestionService;

@RestController
public class QuestionController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping(value = "/intranet/question/{questionUuid}")
    public QuestionDetailModel getQuestionIfDeletable(@PathVariable("questionUuid") UUID questionUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedInUser = loggedinService.getLoggedInUser();
        
        Question entity = questionService.getQuestionIfDeletable(loggedInUser, questionUuid);
        QuestionDetailModel model = QuestionMapper.entityToDomain(entity);
        model.setAnswerDetails(AnswerMapper.entityToDomain(entity.getAnswers()));
        
        return model;
    }
    
    @Transactional
    @PostMapping(value = "/intranet/quiz/{quizUuid}/question")
    public QuestionDetailModel createQuestion(@PathVariable("quizUuid") UUID quizUuid, @RequestBody CreateQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedInUser = loggedinService.getLoggedInUser();
        
        Question entity = questionService.createQuestion(loggedInUser, quizUuid, req);
        QuestionDetailModel model = QuestionMapper.entityToDomain(entity);
        model.setAnswerDetails(AnswerMapper.entityToDomain(entity.getAnswers()));
        
        return model;
    }
    
    @Transactional
    @PutMapping(value = "/intranet/question/{questionUuid}")
    public QuestionDetailModel editQuestion(@PathVariable("questionUuid") UUID questionUuid, @RequestBody EditQuestionRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedInUser = loggedinService.getLoggedInUser();
        
        Question entity = questionService.editQuestion(loggedInUser, questionUuid, req);
        QuestionDetailModel model = QuestionMapper.entityToDomain(entity);
        model.setAnswerDetails(AnswerMapper.entityToDomain(entity.getAnswers()));
        
        return model;
    }
    
    @Transactional
    @DeleteMapping(value = "/intranet/question/{questionUuid}")
    public void deleteQuestion(@PathVariable("questionUuid") UUID questionUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedInUser = loggedinService.getLoggedInUser();
        questionService.deleteQuestion(loggedInUser, questionUuid);
    }

}
