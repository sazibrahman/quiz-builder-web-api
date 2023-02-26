package com.sazibrahman.quizservice.web.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizResponse;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.service.QuizSearchService;
import com.sazibrahman.quizservice.util.DeviceUtil;

@RestController
public class QuizSearchController extends AbstractBaseController {

    @Autowired
    private LoggedInService loggedinService;
    
    @Autowired
    private QuizSearchService quizSearchService;
    
    @PostMapping(value = "/intranet/quiz/search")
    public SearchQuizResponse searchQuizzes(Device device, @RequestBody SearchQuizRequest req) {
    	int pageSize = DeviceUtil.determinePageSize(device);
    	
    	User loggedUser = loggedinService.getLoggedInUser();
        return quizSearchService.searchQuizzes(loggedUser, req, pageSize);
    }

}
