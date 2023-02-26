package com.sazibrahman.quizservice.service;

import com.sazibrahman.quizservice.data.entity.v1.User;

public interface LoggedInService {
    User getLoggedInUser();
}
