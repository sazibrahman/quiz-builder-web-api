package com.sazibrahman.quizservice.data.entity.mapper;

import org.springframework.data.domain.Page;

import com.sazibrahman.quizservice.data.entity.v1.Answer;
import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionLive;

public class QuizSessionLiveMapper {
    
	private QuizSessionLiveMapper() {}

    public static QuizSessionLive entityToDomain(Page<Question> unattemptedQuestions) {
        QuizSessionLive quizSession = new QuizSessionLive();
        quizSession.setPageSize(unattemptedQuestions.getContent().size());
        quizSession.setHasNext(unattemptedQuestions.hasNext());

        for(Question q : unattemptedQuestions.getContent()) {
        	com.sazibrahman.quizservice.data.model.v1.QuizSessionLive.Question qs = new com.sazibrahman.quizservice.data.model.v1.QuizSessionLive.Question();
        	qs.setUuid(q.getUuid());
        	qs.setQuestionText(q.getQuestionText());
        	qs.setQuestionType(q.getQuestionType());
        	
        	for(Answer a : q.getAnswers()) {
        		com.sazibrahman.quizservice.data.model.v1.QuizSessionLive.Answer as = new com.sazibrahman.quizservice.data.model.v1.QuizSessionLive.Answer();
        		as.setUuid(a.getUuid());
        		as.setAnswerText(a.getAnswerText());
        		
        		qs.getAnswers().add(as);
        	}
        	
            quizSession.getQuestions().add(qs);
        }

        return quizSession;
    }
    
}