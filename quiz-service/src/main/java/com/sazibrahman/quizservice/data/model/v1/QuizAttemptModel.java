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
public class QuizAttemptModel {

	private UUID uuid;

    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime createdDate;
    
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
    
}
