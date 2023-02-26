package com.sazibrahman.quizservice.service;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionResponse;

public interface QuizSolutionSearchService {

	SearchQuizSolutionResponse searchQuizSolutionsForPublisher(User loggedUser, SearchQuizSolutionRequest req, int pageSize);
	
	SearchQuizSolutionResponse searchQuizSolutionsForParticipant(User loggedUser, SearchQuizSolutionRequest req, int pageSize);

}
