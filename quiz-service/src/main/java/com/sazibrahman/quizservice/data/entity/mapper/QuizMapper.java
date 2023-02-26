package com.sazibrahman.quizservice.data.entity.mapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.model.v1.CreateQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.QuizItem;
import com.sazibrahman.quizservice.data.model.v1.QuizModel;
import com.sazibrahman.quizservice.util.NameUtil;

public class QuizMapper {
    
	private QuizMapper() {}

    public static Quiz prepareEntityToInsert(CreateQuizRequest req) {
        Quiz entity = new Quiz();
        
        entity.setTitle(req.getTitle());
        entity.setCreatedDate(ZonedDateTime.now());
//        entity.setStartDate(req.getStartDate());
//        entity.setEndDate(req.getEndDate());
        
        return entity;
    }
    
    public static QuizModel entityToDomain(Quiz entity) {
        if (entity == null) {
            return null;
        }
        
        QuizModel domain = new QuizModel();

        domain.setUuid(entity.getUuid());
        domain.setTitle(entity.getTitle());
        
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());

        return domain;
    }
    
    public static QuizItem entityToDomainForSearch(Quiz entity) {
        if (entity == null) {
            return null;
        }
        
        QuizItem domain = new QuizItem();
        domain.setUuid(entity.getUuid());
        domain.setTitle(entity.getTitle());
        domain.setCreatedByFullName(NameUtil.prepareFullName(entity.getCreatedBy()));
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());
        
        return domain;
    }
    
    public static List<QuizItem> entityToDomainForSearch(List<Quiz> entities) {
        return entities.stream().map(e -> entityToDomainForSearch(e)).collect(Collectors.toList());
    }
    
}