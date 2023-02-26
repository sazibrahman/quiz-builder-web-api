package com.sazibrahman.quizservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.repositiry.UserRepository;
import com.sazibrahman.quizservice.service.LoggedInService;
import com.sazibrahman.quizservice.util.UserUtil;

@Service
public class LoggedInServiceImpl implements LoggedInService {

	@Autowired
	private UserRepository userRepository;
	
	public User getLoggedInUser() {
		return userRepository.findByUuid(UserUtil.getLoginUserUuid());
	}

}
