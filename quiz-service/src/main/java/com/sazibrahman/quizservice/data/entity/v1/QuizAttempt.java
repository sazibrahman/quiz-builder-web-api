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
public class QuizAttempt {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attempted_by_uuid")
    private User attemptedBy;
    
    /**
     * VVIP can be NULL
     * - Quiz can be deleted
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_uuid")
    private Quiz quiz;
    
    @Column(name = "created_date", nullable = false)
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime createdDate;
    
    @Column(name = "end_date")
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime endDate;
    
    private double score;
    private double correctPercent;    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quizAttempt", cascade = CascadeType.ALL)
    private List<QuestionAttempt> questionAttempts = new ArrayList<>();

}
