package com.sazibrahman.quizservice.repositiry;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt; 

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID>, JpaSpecificationExecutor<QuestionAttempt> {
	 
	QuestionAttempt findByUuid(UUID uuid);
	
}
