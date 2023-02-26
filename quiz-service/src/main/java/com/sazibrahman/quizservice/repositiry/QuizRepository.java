package com.sazibrahman.quizservice.repositiry;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.Quiz_; 

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID>, JpaSpecificationExecutor<Quiz> {
	 
    Quiz findByUuid(UUID uuid);
    
    static Specification<Quiz> isNotDeleted() {
        return (root, query, cb) -> {
            query.distinct(true);
            
            return cb.isNull(root.get(Quiz_.deletedDate));
        };
    }
    
    static Sort sortByQuizText(Sort.Direction order) {
        return Sort.by(order, "questionText");
    }

    static Sort sortByTitle(Sort.Direction order) {
        return Sort.by(order, Quiz_.title.getName());
    }
}
