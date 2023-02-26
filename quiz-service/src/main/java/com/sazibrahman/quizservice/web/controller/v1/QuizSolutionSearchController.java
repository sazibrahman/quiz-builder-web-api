package com.sazibrahman.quizservice.web.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionResponse;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuizSolutionSearchService;
import com.sazibrahman.quizservice.util.DeviceUtil;

@RestController
public class QuizSolutionSearchController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuizSolutionSearchService quizSolutionSearchService;
    
    @PostMapping(value = "/intranet/quiz-solution/search-by-publisher")
    public SearchQuizSolutionResponse searchQuizSolutionsForPublisher(Device device, @RequestBody SearchQuizSolutionRequest req) {
    	int pageSize = DeviceUtil.determinePageSize(device);
    	
    	User loggedUser = loggedinService.getLoggedInUser();
        return quizSolutionSearchService.searchQuizSolutionsForPublisher(loggedUser, req, pageSize);
    }

    @PostMapping(value = "/intranet/quiz-solution/search-by-participant")
    public SearchQuizSolutionResponse searchQuizSolutionsForParticipant(Device device, @RequestBody SearchQuizSolutionRequest req) {
    	int pageSize = DeviceUtil.determinePageSize(device);
    	
    	User loggedUser = loggedinService.getLoggedInUser();
        return quizSolutionSearchService.searchQuizSolutionsForParticipant(loggedUser, req, pageSize);
    }

}
