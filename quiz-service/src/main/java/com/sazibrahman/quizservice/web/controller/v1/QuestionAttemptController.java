package com.sazibrahman.quizservice.web.controller.v1;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.mapper.QuestionAttemptMapper;
import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionAttemptRequest;
import com.sazibrahman.quizservice.data.model.v1.QuestionAttemptModel;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuestionAttemptService;

@RestController
public class QuestionAttemptController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuestionAttemptService questionAttemptService;
    
    @PostMapping(value = "/intranet/question/{questionUuid}/attempt")
    public QuestionAttemptModel createQuestionAttempt(@PathVariable("questionUuid") UUID questionUuid, @RequestBody CreateQuestionAttemptRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        QuestionAttempt entity = questionAttemptService.createQuestionAttempt(loggedUser, questionUuid, req);
        return QuestionAttemptMapper.entityToDomain(entity);
    }
    
}
