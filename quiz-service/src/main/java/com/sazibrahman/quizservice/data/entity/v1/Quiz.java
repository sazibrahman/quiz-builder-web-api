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
import javax.persistence.OrderBy;

import org.hibernate.annotations.GenericGenerator;

import com.sazibrahman.quizservice.data.entity.converter.ZocalDateTimeToDbUtcConverter;

import lombok.Getter;
import lombok.Setter;  

@Entity
@Setter
@Getter
public class Quiz {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;

	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_uuid")
    private User createdBy;
	   

    private String title;
	
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quiz", cascade = CascadeType.ALL)
    @OrderBy("questionText ASC")
    private List<Question> questions = new ArrayList<>();
    
    @Column(name = "created_date", nullable = false)
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime createdDate;
    
    @Column(name = "start_date")
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime startDate;
    
    @Column(name = "end_date")
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime endDate;
    
    @Column(name = "deleted_date")
    @Convert(converter = ZocalDateTimeToDbUtcConverter.class)
    private ZonedDateTime deletedDate;

}
