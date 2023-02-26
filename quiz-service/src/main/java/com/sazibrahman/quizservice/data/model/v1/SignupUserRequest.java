package com.sazibrahman.quizservice.data.model.v1;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupUserRequest {

    private String email;
    private String password;

    private String firstName;
	private String middleName;
	private String lastName;
	
}
