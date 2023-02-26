package com.sazibrahman.quizservice.repositiry;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.Quiz; 

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
	 
    Question findByUuid(UUID uuid);
    
    Page<Question> findAllByQuizOrderByQuestionText(Quiz quiz, Pageable pageable);
    Page<Question> findAllByQuizAndUuidNotInOrderByQuestionText(Quiz quiz, List<UUID> questionsUuids, Pageable pageable);

}
