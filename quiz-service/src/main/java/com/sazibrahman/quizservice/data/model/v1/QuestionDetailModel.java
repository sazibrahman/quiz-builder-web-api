package com.sazibrahman.quizservice.data.model.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class QuestionDetailModel {

	private UUID uuid;
	
	private QuestionType questionType;
    
    private String questionText;
	
    private List<AnswerDetailModel> answerDetails = new ArrayList<>();

}
