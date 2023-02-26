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

import com.sazibrahman.quizservice.data.entity.mapper.QuizMapper;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.PublishQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.QuizModel;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuizService;

@RestController
public class QuizController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuizService quizService;
    
    @GetMapping(value = "/intranet/quiz/{quizUuid}")
    public QuizModel getQuizIfDeletable(@PathVariable("quizUuid") UUID quizUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        Quiz entity = quizService.getQuizIfDeletable(loggedUser, quizUuid);
        return QuizMapper.entityToDomain(entity);
    }
    
    @Transactional
    @PostMapping(value = "/intranet/quiz")
    // @PreAuthorize("hasAuthority('"+ ConstantUtil.PERMISSION_CREATE_QUIZ + "')")
    public QuizModel createQuiz(@RequestBody CreateQuizRequest req) throws InvalidInputException {
        User loggedInUser = loggedinService.getLoggedInUser();
        Quiz entity = quizService.createQuiz(loggedInUser, req);
        return QuizMapper.entityToDomain(entity);
    }
    
    @Transactional
    @PutMapping(value = "/intranet/quiz/{quizUuid}")
    public QuizModel editQuiz(@PathVariable("quizUuid") UUID quizUuid, @RequestBody EditQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        Quiz entity = quizService.editQuiz(loggedUser, quizUuid, req);
        return QuizMapper.entityToDomain(entity);
    }
    
    @Transactional
    @PutMapping(value = "/intranet/quiz/{quizUuid}/publish")
    public QuizModel publishQuiz(@PathVariable("quizUuid") UUID quizUuid, @RequestBody PublishQuizRequest req) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        Quiz entity = quizService.publishQuiz(loggedUser, quizUuid, req);
        return QuizMapper.entityToDomain(entity);
    }
    
    @Transactional
    @DeleteMapping(value = "/intranet/quiz/{quizUuid}")
    public void deleteQuiz(@PathVariable("quizUuid") UUID quizUuid) throws AuthenticationException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        quizService.deleteQuiz(loggedUser, quizUuid);
    }
    
}
