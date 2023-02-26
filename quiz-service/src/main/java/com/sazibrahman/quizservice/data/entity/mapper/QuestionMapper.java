package com.sazibrahman.quizservice.data.entity.mapper;

import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.QuestionDetailModel;

public class QuestionMapper {
    
	private QuestionMapper() {}

    public static Question prepareEntityToInsert(CreateQuestionRequest req) {
        Question entity = new Question();
        
        entity.setQuestionType(req.getQuestionType());
        entity.setQuestionText(req.getQuestionText());
        
        return entity;
    }
    
    public static Question prepareEntityToEdit(EditQuestionRequest req, Question entity) {
        entity.setQuestionType(req.getQuestionType());
        entity.setQuestionText(req.getQuestionText());
        
        return entity;
    }
    
    public static QuestionDetailModel entityToDomain(Question entity) {
        if (entity == null) {
            return null;
        }
        
        QuestionDetailModel domain = new QuestionDetailModel();

        domain.setUuid(entity.getUuid());
        domain.setQuestionType(entity.getQuestionType());        
        domain.setQuestionText(entity.getQuestionText());

        return domain;
    }
    
}