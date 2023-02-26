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

import com.sazibrahman.quizservice.data.entity.mapper.QuizMapper;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizResponse;
import com.sazibrahman.quizservice.repositiry.QuizRepository;
import com.sazibrahman.quizservice.service.QuizSearchService;

@Service
public class QuizSearchServiceImpl implements QuizSearchService {

    @Autowired
    private QuizRepository quizRepository;
    
    @Override
    public SearchQuizResponse searchQuizzes(User loggedUser, SearchQuizRequest req, int pageSize) {
        List<Specification<Quiz>> specs = new ArrayList<>();
        
        specs.add(QuizRepository.isNotDeleted());
        
        return searchQuizzes(specs, req.getPageNumber(), pageSize);
    }
    
    private SearchQuizResponse searchQuizzes(List<Specification<Quiz>> specs, int pageNumber, int pageSize) {
        Specification<Quiz> querySpec = null;
        if(! CollectionUtils.isEmpty(specs)) {
            querySpec = specs.get(0);
            for (int i = 1; i < specs.size(); i++) {
                querySpec = querySpec.and(specs.get(i));
            }
        }
        
        if(querySpec == null) {
            return new SearchQuizResponse();
        } else {
            pageNumber = pageNumber == 0 ? 0 : pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, QuizRepository.sortByTitle(Direction.ASC));
            Page<Quiz> page = quizRepository.findAll(querySpec, pageRequest);
            
            return new SearchQuizResponse(QuizMapper.entityToDomainForSearch(page.getContent()), page.getTotalElements());
        }
    }
    
}
