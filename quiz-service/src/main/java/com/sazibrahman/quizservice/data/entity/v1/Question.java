package com.sazibrahman.quizservice.data.entity.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;  

@Entity
@Setter
@Getter
public class Question {

    public enum QuestionType {
        SINGLE_CORRECT, MULTI_CORRECT
    }
    
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_uuid")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_uuid")
    private Quiz quiz;
    
    
	
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
	private QuestionType questionType;
    
    private String questionText;
	

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

}
