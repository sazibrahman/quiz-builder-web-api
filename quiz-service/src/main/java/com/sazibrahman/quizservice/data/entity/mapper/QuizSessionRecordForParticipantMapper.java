package com.sazibrahman.quizservice.data.entity.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sazibrahman.quizservice.data.entity.v1.Answer;
import com.sazibrahman.quizservice.data.entity.v1.AnswerAttempt;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant;

public class QuizSessionRecordForParticipantMapper {
    
	private QuizSessionRecordForParticipantMapper() {}

    public static QuizSessionRecordForParticipant entityToDomain(QuizAttempt quizAttempt) {
    	Quiz quiz = quizAttempt.getQuiz();
    	
    	Map<Question, QuestionAttempt> quesAttmpMap = new HashMap<>();
    	Set<Answer> ansSelectedMap = new HashSet<>();
    	for(QuestionAttempt qa : quizAttempt.getQuestionAttempts()) {
    		quesAttmpMap.put(qa.getQuestion(), qa);
    		
    		for(AnswerAttempt aa : qa.getAnswerAttempts()) {
    			ansSelectedMap.add(aa.getAnswer());
    		}
    	}
    	
    	QuizSessionRecordForParticipant record = new QuizSessionRecordForParticipant();
    	
    	record.setQuizAttemptUuid(quizAttempt.getUuid());
    	record.setQuizTitle(quiz.getTitle());
    	record.setCreatedDate(quizAttempt.getCreatedDate());
    	record.setEndDate(quizAttempt.getEndDate());
        record.setScore(quizAttempt.getScore());
        record.setCorrectPercent(quizAttempt.getCorrectPercent());

        for(Question q : quiz.getQuestions()) {
        	com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant.Question qs = new com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant.Question();
        	qs.setUuid(q.getUuid());
        	qs.setQuestionText(q.getQuestionText());
        	qs.setQuestionType(q.getQuestionType());
        	
        	QuestionAttempt qa = quesAttmpMap.get(q);
        	if(qa != null) {
        		qs.setSkipped(qa.isSkipped());
        		qs.setScore(qa.getScore());
        		qs.setCorrectPercent(qa.getCorrectPercent());
        	}
        	
        	for(Answer a : q.getAnswers()) {
        		com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant.Answer as = new com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant.Answer();
        		as.setUuid(a.getUuid());
        		as.setAnswerText(a.getAnswerText());
        		
        		qs.getAnswers().add(as);
        	}
        	
        	record.getQuestions().add(qs);
        }

        
        return record;
    }
    
}