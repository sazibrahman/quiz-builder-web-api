package com.sazibrahman.quizservice.data.model.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionLive {

	private boolean hasNext;
    private int pageSize = 0;
    private List<Question> questions = new ArrayList<>();
	
    @Setter
    @Getter
    public static class Question {
    	private UUID uuid;
    	private QuestionType questionType;
        private String questionText;
        private List<Answer> answers = new ArrayList<>();
    }

    @Setter
    @Getter
    public static class Answer {
    	private UUID uuid;
    	private String answerText;
    }
    
}
