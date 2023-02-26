package com.sazibrahman.quizservice.data.model.v1;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class UserModel {
    
	private UUID uuid;

    private String email;

    private String firstName;
    private String middleName;
    private String lastName;

}
