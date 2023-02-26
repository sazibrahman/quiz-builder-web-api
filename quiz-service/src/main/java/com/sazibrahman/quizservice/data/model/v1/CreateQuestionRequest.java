package com.sazibrahman.quizservice.data.model.v1;

import java.util.ArrayList;
import java.util.List;

import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class CreateQuestionRequest {

	private QuestionType questionType;
    
    private String questionText;
    
    private List<CreateAnswerRequest> answerRequests = new ArrayList<>();

    @Setter
    @Getter
    public static class CreateAnswerRequest {

        private String answerText;
        
        private boolean correct;
        
    }
}
