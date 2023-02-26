package com.sazibrahman.quizservice.data.entity.mapper;

import java.time.ZonedDateTime;

import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.model.v1.QuestionAttemptModel;

public class QuestionAttemptMapper {
    
	private QuestionAttemptMapper() {}

    public static QuestionAttempt prepareEntityToInsert(boolean skipped, double score, double correctPercent) {
    	QuestionAttempt entity = new QuestionAttempt();
        
    	entity.setSkipped(skipped);
        entity.setCreatedDate(ZonedDateTime.now());
        entity.setScore(score);
        entity.setCorrectPercent(correctPercent);
        
        return entity;
    }
    
    public static QuestionAttemptModel entityToDomain(QuestionAttempt entity) {
        if (entity == null) {
            return null;
        }
        
        QuestionAttemptModel domain = new QuestionAttemptModel();

        domain.setUuid(entity.getUuid());
        domain.setQuestionText(entity.getQuestion().getQuestionText());
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setSkipped(entity.isSkipped());
        domain.setScore(entity.getScore());
        domain.setCorrectPercent(entity.getCorrectPercent());

        return domain;
    }
    
}