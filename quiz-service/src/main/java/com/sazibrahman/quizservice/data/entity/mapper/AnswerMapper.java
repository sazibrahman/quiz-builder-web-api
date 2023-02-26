package com.sazibrahman.quizservice.data.entity.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.sazibrahman.quizservice.data.entity.v1.Answer;
import com.sazibrahman.quizservice.data.model.v1.AnswerDetailModel;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest.CreateAnswerRequest;

public class AnswerMapper {
    
	private AnswerMapper() {}

    public static Answer prepareEntityToInsert(CreateAnswerRequest req) {
        Answer entity = new Answer();
        
        entity.setAnswerText(req.getAnswerText());
        entity.setCorrect(req.isCorrect());
        
        return entity;
    }
    
    public static AnswerDetailModel entityToDomain(Answer entity) {
        if (entity == null) {
            return null;
        }
        
        AnswerDetailModel domain = new AnswerDetailModel();

        domain.setUuid(entity.getUuid());
        domain.setAnswerText(entity.getAnswerText());
        entity.setCorrect(entity.isCorrect());

        return domain;
    }
    
    public static List<AnswerDetailModel> entityToDomain(List<Answer> entities) {
        return entities.stream().map(e -> entityToDomain(e)).collect(Collectors.toList());
    }
    
}