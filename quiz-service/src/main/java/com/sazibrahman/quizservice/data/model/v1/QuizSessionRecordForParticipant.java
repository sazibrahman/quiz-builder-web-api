package com.sazibrahman.quizservice.data.model.v1;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;
import com.sazibrahman.quizservice.web.converter.CustomZonedDateTimeDeserializer;
import com.sazibrahman.quizservice.web.converter.CustomZonedDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionRecordForParticipant {

    private UUID quizAttemptUuid;
    private String quizTitle;
    private double score;
    private double correctPercent;
    
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime createdDate;
    
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
	
	private List<Question> questions = new ArrayList<>();
	
    @Setter
    @Getter
    public static class Question {
    	private UUID uuid;
    	private QuestionType questionType;
        private String questionText;
        
        private boolean skipped;
        private double score;
        private double correctPercent;

        private List<Answer> answers = new ArrayList<>();
    }

    @Setter
    @Getter
    public static class Answer {
    	private UUID uuid;
    	private String answerText;
    }
    
}
