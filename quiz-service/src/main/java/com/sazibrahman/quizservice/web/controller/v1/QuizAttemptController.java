package com.sazibrahman.quizservice.web.controller.v1;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.mapper.QuizAttemptMapper;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.QuizAttemptModel;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionLive;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForPublisher;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuizAttemptService;
import com.sazibrahman.quizservice.util.DeviceUtil;

@RestController
public class QuizAttemptController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Transactional
    @PostMapping(value = "/intranet/quiz/{quizUuid}/attempt")
    public QuizAttemptModel createQuizAttempt(@PathVariable("quizUuid") UUID quizUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        QuizAttempt entity = quizAttemptService.createQuizAttempt(loggedUser, quizUuid);
        return QuizAttemptMapper.entityToDomain(entity);
    }    
    
    @GetMapping(value = "/intranet/quiz-attempt/{quizAttemptUuid}/session")
    public QuizSessionLive getNextQuestionSession(Device device, @PathVariable("quizAttemptUuid") UUID quizAttemptUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
    	int pageSize = DeviceUtil.determinePageSize(device);
    	
    	User loggedUser = loggedinService.getLoggedInUser();
        QuizSessionLive model = quizAttemptService.getNextQuestions(loggedUser, quizAttemptUuid, pageSize);
        return model;
    }
    
    @GetMapping(value = "/intranet/quiz-attempt/{quizAttemptUuid}/session-record/publisher")
    public QuizSessionRecordForPublisher getQuizSessionRecordForPublisher(@PathVariable("quizAttemptUuid") UUID quizAttemptUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
    	User loggedUser = loggedinService.getLoggedInUser();
        QuizSessionRecordForPublisher model = quizAttemptService.getQuizSessionRecordForPublisher(loggedUser, quizAttemptUuid);
        return model;
    }

    @GetMapping(value = "/intranet/quiz-attempt/{quizAttemptUuid}/session-record/participant")
    public QuizSessionRecordForParticipant getQuizSessionRecordForParticipant(@PathVariable("quizAttemptUuid") UUID quizAttemptUuid) throws AuthenticationException, InvalidInputException, NotFoundException {
        User loggedUser = loggedinService.getLoggedInUser();
        QuizSessionRecordForParticipant model = quizAttemptService.getQuizSessionRecordForParticipant(loggedUser, quizAttemptUuid);
        return model;
    }

}
