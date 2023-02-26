package com.sazibrahman.quizservice.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.AuthenticationException;

import com.sazibrahman.quizservice.data.entity.v1.Question;
import com.sazibrahman.quizservice.data.entity.v1.QuestionAttempt;
import com.sazibrahman.quizservice.data.entity.v1.Quiz;
import com.sazibrahman.quizservice.data.entity.v1.QuizAttempt;
import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionAttemptRequest;
import com.sazibrahman.quizservice.exception.InvalidInputException;
import com.sazibrahman.quizservice.exception.NotFoundException;
import com.sazibrahman.quizservice.repositiry.AnswerRepository;
import com.sazibrahman.quizservice.repositiry.QuestionAttemptRepository;
import com.sazibrahman.quizservice.repositiry.QuizAttemptRepository;
import com.sazibrahman.quizservice.service.QuestionService;
import com.sazibrahman.quizservice.service.QuizAttemptService;

public class QuestionAttemptServiceImplTest {

    @InjectMocks
    private QuestionAttemptServiceImpl impl;
    
    @Mock
    private QuestionService questionService;
    
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    
    @Mock
    private QuizAttemptService quizAttemptService;
    
    @Mock
    private QuestionAttemptRepository questionAttemptRepository;
    
    @Mock
    private AnswerRepository answerRepository;
    
    @BeforeEach
    public void setUp() {
        impl = new QuestionAttemptServiceImpl();
        MockitoAnnotations.initMocks(this);
    }
    
    @org.junit.jupiter.api.Test
	public void testCreateQuestionAttempt() throws AuthenticationException, NotFoundException, InvalidInputException {
        User user = new User();

        UUID questionUuid1 = UUID.randomUUID();
        UUID answerUuid1 = UUID.randomUUID();
        UUID answerUuid2 = UUID.randomUUID();
        CreateQuestionAttemptRequest req = new CreateQuestionAttemptRequest();
        
        req.setSkipped(true);
        // req.setSelectedAnswerUuids(Arrays.asList(answerUuid1, answerUuid2));        
        
        Quiz mockQuiz = new Quiz();
        
        Question mockQuestion = new Question();
        mockQuestion.setUuid(questionUuid1);
        mockQuestion.setQuestionText("Question 1");
        mockQuestion.setQuiz(mockQuiz);
        
        mockQuiz.setQuestions(Arrays.asList(mockQuestion));
        
        when(questionService.getQuestionIfAttemptable1(any(User.class), any(UUID.class))).thenReturn(mockQuestion);
        
        QuizAttempt mockQuizAttempt = new QuizAttempt();
        mockQuizAttempt.setQuiz(mockQuiz);
        
        when(quizAttemptRepository.findByQuizAndAttemptedBy(any(Quiz.class), any(User.class))).thenReturn(mockQuizAttempt);
        
        Page<Question> unattemptedQuestions = new PageImpl<>(Arrays.asList());
        when(quizAttemptService.getNextUnattemptedQuestions(any(User.class), any(QuizAttempt.class), any(Integer.class))).thenReturn(unattemptedQuestions);
        
        when(questionAttemptRepository.saveAndFlush(any(QuestionAttempt.class))).thenAnswer(i -> i.getArguments()[0]);
        
        QuestionAttempt actualQuestionAttempt = impl.createQuestionAttempt(user, questionUuid1, req);
        
        assertNotNull(actualQuestionAttempt);
	}
    
}
