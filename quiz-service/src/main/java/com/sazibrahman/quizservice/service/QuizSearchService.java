package com.sazibrahman.quizservice.service;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizResponse;

public interface QuizSearchService {

	SearchQuizResponse searchQuizzes(User loggedUser, SearchQuizRequest req, int pageSize);

}
