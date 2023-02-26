package com.sazibrahman.quizservice.repositiry;

import java.util.UUID;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt_;
import com.sazibrahman.quizservice.data.entity.v1.Quiz_;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.entity.v1.User_; 

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID>, JpaSpecificationExecutor<QuizAttempt> {
	 
	QuizAttempt findByUuid(UUID uuid);
    
    QuizAttempt findByQuizAndAttemptedBy(Quiz quiz, User user);
    
    static Specification<QuizAttempt> isNotDeleted() {
        return (root, query, cb) -> {
            query.distinct(true);
            
            final Join<QuizAttempt, Quiz> quizes = root.join(QuizAttempt_.quiz, JoinType.INNER);
            
            return cb.isNull(quizes.get(Quiz_.deletedDate));
        };
    }
    
    static Specification<QuizAttempt> isCreatedByUuidEq(UUID createdByUuid) {
        return (root, query, cb) -> {
            query.distinct(true);
            
            final Join<QuizAttempt, Quiz> quizzes = root.join(QuizAttempt_.quiz, JoinType.INNER);
            final Join<Quiz, User> createdUsers = quizzes.join(Quiz_.createdBy, JoinType.INNER);
            
            return cb.equal(createdUsers.get(User_.uuid), createdByUuid);
        };
    }
    
    static Specification<QuizAttempt> isAttemptedByUuidEq(UUID attemptedByUuid) {
        return (root, query, cb) -> {
            query.distinct(true);
            
            final Join<QuizAttempt, User> attemptedUsers = root.join(QuizAttempt_.attemptedBy, JoinType.INNER);
            
            return cb.equal(attemptedUsers.get(User_.uuid), attemptedByUuid);
        };
    }
    
    static Sort sortByQuizAttemptCreatedDate(Sort.Direction order) {
        return Sort.by(order, QuizAttempt_.createdDate.getName());
    }
	
}
