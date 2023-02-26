package com.sazibrahman.quizservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sazibrahman.quizservice.data.entity.mapper.QuizAttemptMapper;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionResponse;
import com.sazibrahman.quizservice.repositiry.QuizAttemptRepository;
import com.sazibrahman.quizservice.service.QuizSolutionSearchService;

@Service
public class QuizSolutionSearchServiceImpl implements QuizSolutionSearchService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Override
    public SearchQuizSolutionResponse searchQuizSolutionsForPublisher(User loggedUser, SearchQuizSolutionRequest req, int pageSize) {
        List<Specification<QuizAttempt>> specs = new ArrayList<>();
        
        specs.add(QuizAttemptRepository.isNotDeleted());
        specs.add(QuizAttemptRepository.isCreatedByUuidEq(loggedUser.getUuid()));
        
        return searchQuizSolutions(specs, req.getPageNumber(), pageSize);
    }
    
    @Override
    public SearchQuizSolutionResponse searchQuizSolutionsForParticipant(User loggedUser, SearchQuizSolutionRequest req, int pageSize) {
        List<Specification<QuizAttempt>> specs = new ArrayList<>();
        
        specs.add(QuizAttemptRepository.isNotDeleted());
        specs.add(QuizAttemptRepository.isAttemptedByUuidEq(loggedUser.getUuid()));
        
        return searchQuizSolutions(specs, req.getPageNumber(), pageSize);
    }
    
    private SearchQuizSolutionResponse searchQuizSolutions(List<Specification<QuizAttempt>> specs, int pageNumber, int pageSize) {
        Specification<QuizAttempt> querySpec = null;
        if(! CollectionUtils.isEmpty(specs)) {
            querySpec = specs.get(0);
            for (int i = 1; i < specs.size(); i++) {
                querySpec = querySpec.and(specs.get(i));
            }
        }
        
        if(querySpec == null) {
            return new SearchQuizSolutionResponse();
        } else {
            pageNumber = pageNumber == 0 ? 0 : pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, QuizAttemptRepository.sortByQuizAttemptCreatedDate(Direction.ASC));
            Page<QuizAttempt> page = quizAttemptRepository.findAll(querySpec, pageRequest);
            
            return new SearchQuizSolutionResponse(QuizAttemptMapper.entityToDomainForSearch(page.getContent()), page.getTotalElements());
        }
    }
    
}
