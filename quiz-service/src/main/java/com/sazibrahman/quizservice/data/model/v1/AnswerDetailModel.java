package com.sazibrahman.quizservice.data.model.v1;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class AnswerDetailModel {

	private UUID uuid;
	
	private String answerText;
    
	private boolean correct;

}
