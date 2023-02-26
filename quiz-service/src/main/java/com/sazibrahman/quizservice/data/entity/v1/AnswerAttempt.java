package com.sazibrahman.quizservice.data.entity.v1;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;  

@Entity
@Setter
@Getter
public class AnswerAttempt {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_attempt_uuid")
    private QuestionAttempt questionAttempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_uuid")
    private Answer answer;
    
}
