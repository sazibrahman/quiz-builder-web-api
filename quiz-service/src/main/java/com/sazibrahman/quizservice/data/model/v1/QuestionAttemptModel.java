package com.sazibrahman.quizservice.data.model.v1;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sazibrahman.quizservice.web.converter.CustomZonedDateTimeDeserializer;
import com.sazibrahman.quizservice.web.converter.CustomZonedDateTimeSerializer;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class QuestionAttemptModel {

	private UUID uuid;
	
    private String questionText;
	
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime createdDate;

    private boolean skipped;
    private double score;
    private double correctPercent;
    
}
