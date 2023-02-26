package com.sazibrahman.quizservice.data.entity.mapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.model.v1.QuizAttemptModel;
import com.sazibrahman.quizservice.data.model.v1.QuizSolutionItem;
import com.sazibrahman.quizservice.data.model.v1.QuizSolutionItem.QuizSolution;
import com.sazibrahman.quizservice.util.NameUtil;

public class QuizAttemptMapper {
    
	private QuizAttemptMapper() {}

    public static QuizAttempt prepareEntityToInsert() {
    	QuizAttempt entity = new QuizAttempt();
        
        entity.setCreatedDate(ZonedDateTime.now());
//        entity.setEndDate(req.getEndDate());
        
        return entity;
    }
    
    public static QuizAttemptModel entityToDomain(QuizAttempt entity) {
        if (entity == null) {
            return null;
        }
        
        QuizAttemptModel domain = new QuizAttemptModel();

        domain.setUuid(entity.getUuid());
        
        domain.setCreatedDate(entity.getCreatedDate());
        domain.setEndDate(entity.getEndDate());

        return domain;
    }
    
    
    
    public static QuizSolutionItem entityToDomainForSearch(QuizAttempt entity) {
        if (entity == null) {
            return null;
        }
        
        Quiz entityQuiz = entity.getQuiz();

        com.sazibrahman.quizservice.data.model.v1.QuizSolutionItem.Quiz domainQuiz = new com.sazibrahman.quizservice.data.model.v1.QuizSolutionItem.Quiz();
        domainQuiz.setUuid(entityQuiz.getUuid());
        domainQuiz.setTitle(entityQuiz.getTitle());
        domainQuiz.setCreatedByFullName(NameUtil.prepareFullName(entityQuiz.getCreatedBy()));
        domainQuiz.setCreatedDate(entityQuiz.getCreatedDate());
        domainQuiz.setStartDate(entityQuiz.getStartDate());
        domainQuiz.setEndDate(entityQuiz.getEndDate());
        
        QuizSolution domainSolution = new QuizSolution();
        domainSolution.setUuid(entity.getUuid());
        domainSolution.setAttemptedByFullName(NameUtil.prepareFullName(entity.getAttemptedBy()));
        domainSolution.setCreatedDate(entity.getCreatedDate());
        domainSolution.setEndDate(entity.getEndDate());
        domainSolution.setScore(entity.getScore());
        domainSolution.setCorrectPercent(entity.getCorrectPercent());
        
        QuizSolutionItem domain = new QuizSolutionItem();
        domain.setQuiz(domainQuiz);
        domain.setSolution(domainSolution);

        return domain;
    }
    
    public static List<QuizSolutionItem> entityToDomainForSearch(List<QuizAttempt> entities) {
        return entities.stream().map(e -> entityToDomainForSearch(e)).collect(Collectors.toList());
    }
    
}