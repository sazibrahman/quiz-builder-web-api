package com.sazibrahman.quizservice.repositiry;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.Answer; 

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID>, JpaSpecificationExecutor<Answer> {
	 
    Answer findByUuid(UUID uuid);
	
}
