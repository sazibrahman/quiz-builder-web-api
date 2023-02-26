package com.sazibrahman.quizservice.data.entity.v1;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import com.sazibrahman.quizservice.data.entity.converter.ZocalDateTimeToDbUtcConverter;

import lombok.Getter;
import lombok.Setter;  

@Entity
@Setter
@Getter
public class QuestionAttempt {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;
	
//    @Column(name = "attempted_by_uuid")
//    private User attemptedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_uuid")
    private QuizAttempt quizAttempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_uuid")
    private Question question;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionAttempt", cascade = CascadeType.ALL)
    private List<AnswerAttempt> answerAttempts = new ArrayList<>();
    
    @Column(name = "created_date", nullable = false)
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime createdDate;
    
    private boolean skipped;
    private double score;
    private double correctPercent;

}
