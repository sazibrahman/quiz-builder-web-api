package com.sazibrahman.quizservice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sazibrahman.quizservice.data.entity.v1.Question.QuestionType;
import com.sazibrahman.quizservice.data.model.v1.ApiError;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionAttemptRequest;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.CreateQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuestionRequest;
import com.sazibrahman.quizservice.data.model.v1.EditQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.PublishQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.QuestionAttemptModel;
import com.sazibrahman.quizservice.data.model.v1.QuestionDetailModel;
import com.sazibrahman.quizservice.data.model.v1.QuizAttemptModel;
import com.sazibrahman.quizservice.data.model.v1.QuizItem;
import com.sazibrahman.quizservice.data.model.v1.QuizModel;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionLive;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForParticipant;
import com.sazibrahman.quizservice.data.model.v1.QuizSessionRecordForPublisher;
import com.sazibrahman.quizservice.data.model.v1.QuizSolutionItem;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizResponse;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionRequest;
import com.sazibrahman.quizservice.data.model.v1.SearchQuizSolutionResponse;
import com.sazibrahman.quizservice.data.model.v1.SignupUserRequest;
import com.sazibrahman.quizservice.data.model.v1.UserModel;
import com.sazibrahman.quizservice.data.model.v1.CreateQuestionRequest.CreateAnswerRequest;
import com.sazibrahman.quizservice.web.Application;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtAuthenticationRequest;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtAuthenticationResponse;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class ApplicationIntegrationTest {
    private static final Map<String, String> desktopHeaderMap = new HashMap<String, String>() {
		private static final long serialVersionUID = -8490653642862613645L;
	{
    	put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
    }};
    private static final Map<String, String> mobileHeaderMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 8726736827112506852L;
	{
    	put("User-Agent", "Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.35+ (KHTML, like Gecko) Version/10.3.3.2205 Mobile Safari/537.35+");
    }};
    
    private static final String ques1MoonIsStar = "Moon is a star";
    private static final String yes = "Yes";
    private static final String no = "No";

    private static final String quest2TempCanBeMeasuredIn = "Temperature can be measured in";
    private static final String kelvin = "Kelvin";
    private static final String fahrenheit = "Fahrenheit";
    private static final String gram = "Gram";
    private static final String celsius = "Celsius";
    private static final String liters = "Liters";

    private static boolean hasInit = false;
    private static String user1Token = null;
    private static String user2Token = null;
    private static String user3Token = null;
    
    private static UUID publishedQuizUuid = null;
    private static UUID publishedQuizQuestion1Uuid = null;
    private static UUID publishedQuizQuestion2Uuid = null;
    
    private static UUID unpublishedQuizUuid = null;
    private static UUID unpublishedQuizQuestion1Uuid = null;
    private static UUID unpublishedQuizQuestion2Uuid = null;
    
    private static UUID deletedQuizUuid = null;
    private static UUID deletedQuizQuestion1Uuid = null;
    private static UUID deletedQuizQuestion2Uuid = null;
    
    private static ObjectMapper objectMapper = new ObjectMapper();

	@Container
    @SuppressWarnings("deprecation")
    private static MySQLContainer<?> database = new MySQLContainer<>();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    /**
     * User#1:
     * - signs up
     * - creates and publishes PublishedQuiz
     * - creates UnpublishedQuiz
     * - creates and deletes DeletedQuiz
     */
    @BeforeEach
    public void init() throws Exception {
        if(hasInit) {
           return;
        }
        
        // ---------------- User#1 signs up --------------------
        SignupUserRequest signupUser1Req = prepareSignupUserRequest("sazib.rahman1@gmail.com", "Passw0rd!", "Saifur", "Rahman");
        doPost("/sign-up", null, signupUser1Req, UserModel.class);
        
        // ---------------- User#1 logs in --------------------
        JwtAuthenticationResponse user1AuthRes = doPost("/auth", null, new JwtAuthenticationRequest("sazib.rahman1@gmail.com", "Passw0rd!"), JwtAuthenticationResponse.class);
        user1Token = user1AuthRes.getToken();

        // ---------------- User#1 creates PublishedQuiz, adds Question, and publishes ----------------------
        Triple<UUID, UUID, UUID> quizResult = submitQuiz(user1Token, "PublishedQuiz");
        publishedQuizUuid = quizResult.getLeft();
        publishedQuizQuestion1Uuid = quizResult.getMiddle();
        publishedQuizQuestion2Uuid = quizResult.getRight();
        
        PublishQuizRequest publishQuizRequest = preparePublishQuizRequest(ZonedDateTime.now(), null);
        doPut("/intranet/quiz/" + publishedQuizUuid + "/publish", user1Token, publishQuizRequest, QuizModel.class);

        // ---------------- User#1 creates UnpublishedQuiz and adds Question ----------------------
        quizResult = submitQuiz(user1Token, "UnpublishedQuiz");
        unpublishedQuizUuid = quizResult.getLeft();
        unpublishedQuizQuestion1Uuid = quizResult.getMiddle();
        unpublishedQuizQuestion2Uuid = quizResult.getRight();

        // ---------------- User#1 creates DeletedQuiz and deletes ----------------------
        quizResult = submitQuiz(user1Token, "UnpublishedQuiz");
        deletedQuizUuid = quizResult.getLeft();
        deletedQuizQuestion1Uuid = quizResult.getMiddle();
        deletedQuizQuestion2Uuid = quizResult.getRight();

        doDelete("/intranet/quiz/" + deletedQuizUuid, user1Token, null);
        
        
        
        
        // ---------------- User#2 signs up --------------------
        SignupUserRequest signupUser2Req = prepareSignupUserRequest("sazib.rahman2@gmail.com", "Passw0rd!", "Saifur", "Rahman");
        UserModel user2 = doPost("/sign-up", null, signupUser2Req, UserModel.class);
        assertThat(user2.getUuid(), notNullValue());
        
        // ---------------- User#2 logs in --------------------
        JwtAuthenticationResponse user2AuthRes = doPost("/auth", null, new JwtAuthenticationRequest("sazib.rahman2@gmail.com", "Passw0rd!"), JwtAuthenticationResponse.class);
        assertThat(user2AuthRes.getToken(), notNullValue());
        user2Token = user2AuthRes.getToken();

        

        
        // ---------------- User#3 signs up --------------------
        SignupUserRequest signupUser3Req = prepareSignupUserRequest("sazib.rahman3@gmail.com", "Passw0rd!", "Saifur", "Rahman");
        UserModel user3 = doPost("/sign-up", null, signupUser3Req, UserModel.class);
        assertThat(user3.getUuid(), notNullValue());
        
        // ---------------- User#2 logs in --------------------
        JwtAuthenticationResponse user3AuthRes = doPost("/auth", null, new JwtAuthenticationRequest("sazib.rahman3@gmail.com", "Passw0rd!"), JwtAuthenticationResponse.class);
        assertThat(user3AuthRes.getToken(), notNullValue());
        user3Token = user3AuthRes.getToken();
        
        
        hasInit = true;
    }
    
    @Test
    public void testSignup_invalidSignup() throws Exception {
        // ---------------- Signs up fails--------------------
        SignupUserRequest signupUser1Req = prepareSignupUserRequest("sazib.rahman1@gmail.com", "Passw0rd!", "Saifur", "Rahman");
        ApiError apiError = doPostError("/sign-up", null, signupUser1Req, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    public void testAuth_invalidLogin() throws Exception {
        // ---------------- Login fails --------------------
        ApiError apiError = doPostError("/auth", null, new JwtAuthenticationRequest("sazib.rahman@gmail.com", "Passw0rd!"), status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
    }
    
    @Test
    public void testUser1_failsToModifyPublishedQuiz() throws Exception {
        // ---------------- User#1 gets PublishedQuiz ----------------------
        QuizModel getQuizModel = doGet("/intranet/quiz/" + publishedQuizUuid, user1Token, QuizModel.class);
        assertThat(getQuizModel.getUuid(), equalTo(publishedQuizUuid));

        // ---------------- User#1 fails to edit PublishedQuiz ----------------------
        String quiz1TitleUpdated = "Quiz#1 - Updated Title 2nd Time";
        EditQuizRequest editQuizRequest = prepareEditQuizRequest(quiz1TitleUpdated);
        ApiError apiError = doPutError("/intranet/quiz/" + publishedQuizUuid, user1Token, editQuizRequest, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        
        // ---------------- User#1 gets Question ----------------------
        QuestionDetailModel getQuestionDetailModel = doGet("/intranet/question/" + publishedQuizQuestion1Uuid, user1Token, QuestionDetailModel.class);
        assertThat(getQuestionDetailModel.getUuid(), equalTo(publishedQuizQuestion1Uuid));
        
        // ---------------- User#1 fails to add Question to Quiz#1 ----------------------
        CreateQuestionRequest createQuestionRequest = prepareCreateQuestionRequest(QuestionType.SINGLE_CORRECT, "You love Java", Arrays.asList(
                Pair.of(yes, false),
                Pair.of(no, true)
        ));
        apiError = doPostError("/intranet/quiz/" + publishedQuizUuid + "/question", user1Token, createQuestionRequest, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        
        // ---------------- User#1 fails to delete Question of Quiz#1 ----------------------
        apiError = doDeleteError("/intranet/question/" + publishedQuizQuestion1Uuid, user1Token, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        
        // ---------------- User#1 fails to edit Question of Quiz#1 ----------------------
        EditQuestionRequest editTempQuestionRequest = prepareEditQuestionRequest(QuestionType.MULTI_CORRECT, quest2TempCanBeMeasuredIn, Arrays.asList(
                Pair.of(kelvin, true),
                Pair.of(fahrenheit, true),
                Pair.of(gram, false),
                Pair.of(celsius, true),
                Pair.of(liters, false)
        ));
        apiError = doPutError("/intranet/question/" + publishedQuizQuestion1Uuid, user1Token, editTempQuestionRequest, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
    }
    
    @Test
    public void testUser1_failsToModifyDeletedQuiz() throws Exception {
        // ---------------- User#1 fails to get DeletedQuiz ----------------------
    	ApiError apiError = doGetError("/intranet/quiz/" + deletedQuizUuid, user1Token, status().isNotFound(), ApiError.class);
    	assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));

        // ---------------- User#1 fails edit DeletedQuiz ----------------------
        String quiz1TitleUpdated = "Quiz#1 - Updated Title 2nd Time";
        EditQuizRequest editQuizRequest = prepareEditQuizRequest(quiz1TitleUpdated);
        apiError = doPutError("/intranet/quiz/" + deletedQuizUuid, user1Token, editQuizRequest, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        
        // ---------------- User#1 fails to get Question of DeletedQuiz ----------------------
        apiError = doGetError("/intranet/question/" + deletedQuizQuestion1Uuid, user1Token, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        
        // ---------------- User#1 fails to add Question to DeletedQuiz ----------------------
        CreateQuestionRequest createQuestionRequest = prepareCreateQuestionRequest(QuestionType.SINGLE_CORRECT, ques1MoonIsStar, Arrays.asList(
                Pair.of(yes, false),
                Pair.of(no, true)
        ));
        apiError = doPostError("/intranet/quiz/" + deletedQuizUuid + "/question", user1Token, createQuestionRequest, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        
        // ---------------- User#1 fails to delete Question of DeletedQuiz ----------------------
        apiError = doDeleteError("/intranet/question/" + deletedQuizQuestion1Uuid, user1Token, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        
        // ---------------- User#1 fails to edit Question of DeletedQuiz ----------------------
        EditQuestionRequest editTempQuestionRequest = prepareEditQuestionRequest(QuestionType.MULTI_CORRECT, quest2TempCanBeMeasuredIn, Arrays.asList(
                Pair.of(kelvin, true),
                Pair.of(fahrenheit, true),
                Pair.of(gram, false),
                Pair.of(celsius, true),
                Pair.of(liters, false)
        ));
        apiError = doPutError("/intranet/question/" + deletedQuizQuestion1Uuid, user1Token, editTempQuestionRequest, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
    }
    
    @Test
    public void testUser1_modifiesUnpublishedQuiz() throws Exception {
        // ---------------- User#1 gets PublishedQuiz ----------------------
        QuizModel getQuizModel = doGet("/intranet/quiz/" + unpublishedQuizUuid, user1Token, QuizModel.class);
        assertThat(getQuizModel.getUuid(), equalTo(unpublishedQuizUuid));
        
        // ---------------- User#1 edits UnpublishedQuiz ----------------------
        String quizTitleUpdated = "UnpublishedQuiz - Updated Title";
        EditQuizRequest editQuizRequest = prepareEditQuizRequest(quizTitleUpdated);
        QuizModel updatedQuizModel = doPut("/intranet/quiz/" + unpublishedQuizUuid, user1Token, editQuizRequest, QuizModel.class);
        assertThat(updatedQuizModel.getUuid(), equalTo(unpublishedQuizUuid));
        assertThat(updatedQuizModel.getTitle(), equalTo(quizTitleUpdated));
        
        getQuizModel = doGet("/intranet/quiz/" + unpublishedQuizUuid, user1Token, QuizModel.class);
        assertThat(getQuizModel.getUuid(), equalTo(unpublishedQuizUuid));
        assertThat(getQuizModel.getTitle(), equalTo(quizTitleUpdated));
        
        // ---------------- User#1 adds Question to UnpublishedQuiz ----------------------
        String quesTitle = "You love Java";
        CreateQuestionRequest createMoonQuestionRequest = prepareCreateQuestionRequest(QuestionType.SINGLE_CORRECT, quesTitle, Arrays.asList(
                Pair.of(yes, false),
                Pair.of(no, true)
        ));
        QuestionDetailModel createdQuestionDetailModel = doPost("/intranet/quiz/" + unpublishedQuizUuid + "/question", user1Token, createMoonQuestionRequest, QuestionDetailModel.class);
        assertThat(createdQuestionDetailModel.getUuid(), notNullValue());
        assertThat(createdQuestionDetailModel.getQuestionText(), equalTo(quesTitle));
        assertThat(createdQuestionDetailModel.getAnswerDetails().stream().map(a -> a.getAnswerText()).collect(Collectors.toList()), containsInAnyOrder(yes, no));
        
        QuestionDetailModel getMoonQuestionDetailModel = doGet("/intranet/question/" + createdQuestionDetailModel.getUuid(), user1Token, QuestionDetailModel.class);
        assertThat(getMoonQuestionDetailModel.getUuid(), equalTo(createdQuestionDetailModel.getUuid()));
        assertThat(getMoonQuestionDetailModel.getQuestionText(), equalTo(quesTitle));
        assertThat(getMoonQuestionDetailModel.getAnswerDetails().stream().map(a -> a.getAnswerText()).collect(Collectors.toList()), containsInAnyOrder(yes, no));
        
        // ---------------- User#1 edits Question ----------------------
        String newTitle = "What are planets?";
        String earth = "Earth";
        String moon = "Moon";
        String sun = "Sun";
        String mars = "Mars";
        EditQuestionRequest editQuestionRequest = prepareEditQuestionRequest(QuestionType.MULTI_CORRECT, newTitle, Arrays.asList(
                Pair.of(earth, true),
                Pair.of(moon, false),
                Pair.of(sun, false),
                Pair.of(mars, true)
        ));
        QuestionDetailModel updatedTempQuestionDetailModel = doPut("/intranet/question/" + createdQuestionDetailModel.getUuid(), user1Token, editQuestionRequest, QuestionDetailModel.class);
        assertThat(updatedTempQuestionDetailModel.getUuid(), notNullValue());
        assertThat(updatedTempQuestionDetailModel.getQuestionText(), equalTo(newTitle));
        assertThat(updatedTempQuestionDetailModel.getAnswerDetails().stream().map(a -> a.getAnswerText()).collect(Collectors.toList()), containsInAnyOrder(earth, moon, sun, mars));
        
        QuestionDetailModel getQuestionDetailModel = doGet("/intranet/question/" + createdQuestionDetailModel.getUuid(), user1Token, QuestionDetailModel.class);
        assertThat(getQuestionDetailModel.getUuid(), equalTo(createdQuestionDetailModel.getUuid()));
        assertThat(getQuestionDetailModel.getQuestionText(), equalTo(newTitle));
        assertThat(getQuestionDetailModel.getAnswerDetails().stream().map(a -> a.getAnswerText()).collect(Collectors.toList()), containsInAnyOrder(earth, moon, sun, mars));

        // ---------------- User#1 deletes Question ----------------------
        doDelete("/intranet/question/" + createdQuestionDetailModel.getUuid(), user1Token, null);
        
        ApiError apiError = doGetError("/intranet/question/" + createdQuestionDetailModel.getUuid(), user1Token, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
    }
    
    @Test
    public void testUser2_failsToModifyUnpublishedQuiz() throws Exception {
        // ---------------- User#2 fails to get Quiz#1 ----------------------
        ApiError apiError = doGetError("/intranet/quiz/" + unpublishedQuizUuid, user2Token, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));

        // ---------------- User#2 fails edit Quiz#1 ----------------------
        String quiz1TitleUpdated = "Quiz#1 - Updated Title 2nd Time";
        EditQuizRequest editQuizRequest = prepareEditQuizRequest(quiz1TitleUpdated);
        apiError = doPutError("/intranet/quiz/" + unpublishedQuizUuid, user2Token, editQuizRequest, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
        
        // ---------------- User#2 fails to delete Quiz#1 ----------------------
        apiError = doDeleteError("/intranet/quiz/" + unpublishedQuizUuid, user2Token, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
        
        // ---------------- User#2 fails to get Question of Quiz#1  ----------------------
        apiError = doGetError("/intranet/question/" + unpublishedQuizQuestion1Uuid, user2Token, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));

        // ---------------- User#2 fails to add Question to Quiz#1 ----------------------
        CreateQuestionRequest createMoonQuestionRequest = prepareCreateQuestionRequest(QuestionType.SINGLE_CORRECT, ques1MoonIsStar, Arrays.asList(
                Pair.of(yes, false),
                Pair.of(no, true)
        ));
        apiError = doPostError("/intranet/quiz/" + unpublishedQuizUuid + "/question", user2Token, createMoonQuestionRequest, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
        
        // ---------------- User#2 fails to delete Question of Quiz#1 ----------------------
        apiError = doDeleteError("/intranet/question/" + unpublishedQuizQuestion1Uuid, user2Token, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
        
        // ---------------- User#2 fails to edit Question of Quiz#1 ----------------------
        EditQuestionRequest editTempQuestionRequest = prepareEditQuestionRequest(QuestionType.MULTI_CORRECT, quest2TempCanBeMeasuredIn, Arrays.asList(
                Pair.of(kelvin, true),
                Pair.of(fahrenheit, true),
                Pair.of(gram, false),
                Pair.of(celsius, true),
                Pair.of(liters, false)
        ));
        apiError = doPutError("/intranet/question/" + unpublishedQuizQuestion1Uuid, user2Token, editTempQuestionRequest, status().isUnauthorized(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
    }
    
    @Test
    public void testUser2_failsToAttemptUnpublishedQuiz() throws Exception {
        // ---------------- User#2 fails to attempt UnpublishedQuiz ----------------------
        ApiError apiError = doPostError("/intranet/quiz/" + unpublishedQuizUuid + "/attempt", user2Token, null, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void testUser2_failsToAttemptDeletedQuiz() throws Exception {
        // ---------------- User#2 fails to attempt DeletedQuiz ----------------------
        ApiError apiError = doPostError("/intranet/quiz/" + deletedQuizUuid + "/attempt", user2Token, null, status().isNotFound(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
    }
    
    private QuizSessionLive.Answer findAnswerByContent(QuizSessionLive.Question ques, String ansContent) {
    	for(QuizSessionLive.Answer a : ques.getAnswers()) {
    		if(a.getAnswerText().equals(ansContent)) {
    			return a;
    		}
    	}
    	
    	return null;
    }
    

    
    @Test
    public void testUser2_attemptPublishedQuiz_usingMobile() throws Exception {
    	// ---------------- User#2 attempts PublishedQuiz ----------------------
        QuizAttemptModel quizAttemptModel = doPost("/intranet/quiz/" + publishedQuizUuid + "/attempt", user2Token, null, QuizAttemptModel.class);
        assertThat(quizAttemptModel.getUuid(), notNullValue());
        
        // ---------------- User#2 gets next question ----------------------
        QuizSessionLive quizSessionModel = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session", user2Token, QuizSessionLive.class, mobileHeaderMap);
        assertThat(quizSessionModel.isHasNext(), equalTo(true));
        assertThat(quizSessionModel.getPageSize(), equalTo(1));
        
        // ---------------- User#2 attempts question ----------------------
        QuizSessionLive.Question ques1 = quizSessionModel.getQuestions().get(0);
        QuizSessionLive.Answer ansYes = findAnswerByContent(ques1, yes);
        
        CreateQuestionAttemptRequest createQuestionAttemptRequest = new CreateQuestionAttemptRequest();
        createQuestionAttemptRequest.setSkipped(true);
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansYes.getUuid()));
        QuestionAttemptModel questionAttemptModel = doPost("/intranet/question/" + ques1.getUuid() + "/attempt", user2Token, createQuestionAttemptRequest, QuestionAttemptModel.class);
        assertThat(questionAttemptModel.getQuestionText(), equalTo(ques1.getQuestionText()));
        assertThat(questionAttemptModel.isSkipped(), equalTo(true));
        // assertTrue(questionAttemptModel.getScore() - 0.0 == 0);
        
        // ---------------- User#2 gets next question ----------------------
        quizSessionModel = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session", user2Token, QuizSessionLive.class, mobileHeaderMap);
        assertThat(quizSessionModel.isHasNext(), equalTo(false));
        assertThat(quizSessionModel.getPageSize(), equalTo(1));

        // ---------------- User#2 attempts question ----------------------
        QuizSessionLive.Question ques2 = quizSessionModel.getQuestions().get(0);
        QuizSessionLive.Answer ansKelvin = findAnswerByContent(ques2, kelvin);
        QuizSessionLive.Answer ansFahrenheit = findAnswerByContent(ques2, fahrenheit);
        QuizSessionLive.Answer ansCelsius = findAnswerByContent(ques2, celsius);
        QuizSessionLive.Answer ansGram = findAnswerByContent(ques2, gram);
        QuizSessionLive.Answer ansLiters = findAnswerByContent(ques2, liters);
        
        createQuestionAttemptRequest = new CreateQuestionAttemptRequest();
        createQuestionAttemptRequest.setSkipped(false);
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid())); // 0.66 => 66%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansCelsius.getUuid())); // 1 => 100%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansGram.getUuid())); // 0.166 => 16.6%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansCelsius.getUuid(), ansGram.getUuid(), ansLiters.getUuid())); // 0 => 0%
        createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansGram.getUuid(), ansLiters.getUuid())); // -1 => -100%
        questionAttemptModel = doPost("/intranet/question/" + ques2.getUuid() + "/attempt", user2Token, createQuestionAttemptRequest, QuestionAttemptModel.class);
        assertThat(questionAttemptModel.getQuestionText(), equalTo(ques2.getQuestionText()));
        assertThat(questionAttemptModel.isSkipped(), equalTo(false));
        // assertTrue(questionAttemptModel.getScore() - 0.00 != 0);

        // ---------------- User#2 gets next question ----------------------
        quizSessionModel = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session", user2Token, QuizSessionLive.class);
        assertThat(quizSessionModel.isHasNext(), equalTo(false));
        assertThat(quizSessionModel.getPageSize(), equalTo(0));
        
        // ---------------- User#2 gets record ----------------------
        QuizSessionRecordForParticipant quizSessionRecordForParticipant = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session-record/participant", user2Token, QuizSessionRecordForParticipant.class);
        assertThat(quizSessionRecordForParticipant.getQuizAttemptUuid(), equalTo(quizAttemptModel.getUuid()));
        // assertTrue(quizSessionRecordForParticipant.getScore() > 0);
        
        // ---------------- User#1 gets full record ----------------------
        QuizSessionRecordForPublisher quizSessionRecordForPublisher = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session-record/publisher", user1Token, QuizSessionRecordForPublisher.class);
        assertThat(quizSessionRecordForPublisher.getQuizAttemptUuid(), equalTo(quizAttemptModel.getUuid()));
        // assertTrue(quizSessionRecordForPublisher.getScore() > 0);
        
    	// ---------------- User#2 fails to re-attempt PublishedQuiz ----------------------
        ApiError apiError = doPostError("/intranet/quiz/" + publishedQuizUuid + "/attempt", user2Token, null, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        
        
        
        
        
        
        // ---------------- User#2 searches for his solutions ----------------------
    	SearchQuizSolutionRequest req = new SearchQuizSolutionRequest();
    	req.setPageNumber(0);
    	
    	SearchQuizSolutionResponse res = doPost("/intranet/quiz-solution/search-by-participant", user2Token, req, SearchQuizSolutionResponse.class);
    	assertThat(res.getItems().size(), equalTo(1));
    	
    	QuizSolutionItem item = res.getItems().get(0);
        assertThat(item.getQuiz().getUuid(), equalTo(publishedQuizUuid));
        
        // ---------------- User#1 searches for solutions of his published quizzes ----------------------
    	res = doPost("/intranet/quiz-solution/search-by-publisher", user1Token, req, SearchQuizSolutionResponse.class);
    	assertThat(res.getItems().size(), equalTo(2));
    	
    	item = res.getItems().get(0);
        assertThat(item.getQuiz().getUuid(), equalTo(publishedQuizUuid));
        
        
        
        
        // ---------------- User#2 searches all quizzes ----------------------
    	SearchQuizRequest searchQuizRequest = new SearchQuizRequest();
    	searchQuizRequest.setPageNumber(0);
    	
    	SearchQuizResponse searchQuizResponse = doPost("/intranet/quiz/search", user2Token, searchQuizRequest, SearchQuizResponse.class);
    	assertThat(searchQuizResponse.getItems().size(), equalTo(2));
    	
    	QuizItem quizItem = searchQuizResponse.getItems().get(0);
        assertThat(quizItem.getUuid(), notNullValue());
    }
    
    @Test
    public void testUser3_attemptPublishedQuiz_usingDesktop() throws Exception {
    	// ---------------- User#3 attempts PublishedQuiz ----------------------
        QuizAttemptModel quizAttemptModel = doPost("/intranet/quiz/" + publishedQuizUuid + "/attempt", user3Token, null, QuizAttemptModel.class);
        assertThat(quizAttemptModel.getUuid(), notNullValue());
        
        // ---------------- User#3 gets next question ----------------------
        QuizSessionLive quizSessionModel = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session", user3Token, QuizSessionLive.class, desktopHeaderMap);
        assertThat(quizSessionModel.isHasNext(), equalTo(false));
        assertThat(quizSessionModel.getPageSize(), equalTo(2));
        
        // ---------------- User#3 attempts question ----------------------
        QuizSessionLive.Question ques1 = quizSessionModel.getQuestions().get(0);
        QuizSessionLive.Answer ansYes = findAnswerByContent(ques1, yes);
        
        CreateQuestionAttemptRequest createQuestionAttemptRequest = new CreateQuestionAttemptRequest();
        createQuestionAttemptRequest.setSkipped(true);
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansYes.getUuid()));
        QuestionAttemptModel questionAttemptModel = doPost("/intranet/question/" + ques1.getUuid() + "/attempt", user3Token, createQuestionAttemptRequest, QuestionAttemptModel.class);
        assertThat(questionAttemptModel.getQuestionText(), equalTo(ques1.getQuestionText()));
        assertThat(questionAttemptModel.isSkipped(), equalTo(true));
        // assertTrue(questionAttemptModel.getScore() - 0.0 == 0);
        
        // ---------------- User#3 attempts question ----------------------
        QuizSessionLive.Question ques2 = quizSessionModel.getQuestions().get(1);
        QuizSessionLive.Answer ansKelvin = findAnswerByContent(ques2, kelvin);
        QuizSessionLive.Answer ansFahrenheit = findAnswerByContent(ques2, fahrenheit);
        QuizSessionLive.Answer ansCelsius = findAnswerByContent(ques2, celsius);
        QuizSessionLive.Answer ansGram = findAnswerByContent(ques2, gram);
        QuizSessionLive.Answer ansLiters = findAnswerByContent(ques2, liters);
        
        createQuestionAttemptRequest = new CreateQuestionAttemptRequest();
        // createQuestionAttemptRequest.setSkipped(false);
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid())); // 0.66 => 66%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansCelsius.getUuid())); // 1 => 100%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansGram.getUuid())); // 0.166 => 16.6%
        // createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansKelvin.getUuid(), ansFahrenheit.getUuid(), ansCelsius.getUuid(), ansGram.getUuid(), ansLiters.getUuid())); // 0 => 0%
        createQuestionAttemptRequest.setSelectedAnswerUuids(Arrays.asList(ansGram.getUuid(), ansLiters.getUuid())); // -1 => -100%
        questionAttemptModel = doPost("/intranet/question/" + ques2.getUuid() + "/attempt", user3Token, createQuestionAttemptRequest, QuestionAttemptModel.class);
        assertThat(questionAttemptModel.getQuestionText(), equalTo(ques2.getQuestionText()));
        assertThat(questionAttemptModel.isSkipped(), equalTo(false));
        // assertTrue(questionAttemptModel.getScore() - 0.00 != 0);

        // ---------------- User#3 gets next question ----------------------
        quizSessionModel = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session", user3Token, QuizSessionLive.class);
        assertThat(quizSessionModel.isHasNext(), equalTo(false));
        assertThat(quizSessionModel.getPageSize(), equalTo(0));
        
        // ---------------- User#3 gets record ----------------------
        QuizSessionRecordForParticipant quizSessionRecordForParticipant = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session-record/participant", user3Token, QuizSessionRecordForParticipant.class);
        assertThat(quizSessionRecordForParticipant.getQuizAttemptUuid(), equalTo(quizAttemptModel.getUuid()));
        // assertTrue(quizSessionRecordForParticipant.getScore() > 0);
        
        // ---------------- User#1 gets full record ----------------------
        QuizSessionRecordForPublisher quizSessionRecordForPublisher = doGet("/intranet/quiz-attempt/" + quizAttemptModel.getUuid() + "/session-record/publisher", user1Token, QuizSessionRecordForPublisher.class);
        assertThat(quizSessionRecordForPublisher.getQuizAttemptUuid(), equalTo(quizAttemptModel.getUuid()));
        // assertTrue(quizSessionRecordForPublisher.getScore() > 0);
        
    	// ---------------- User#3 fails to re-attempt PublishedQuiz ----------------------
        ApiError apiError = doPostError("/intranet/quiz/" + publishedQuizUuid + "/attempt", user3Token, null, status().isConflict(), ApiError.class);
        assertThat(apiError.getStatus(), equalTo(HttpStatus.CONFLICT.value()));
        
        
        
        
        
        
        
        // ---------------- User#3 searches for his solutions ----------------------
    	SearchQuizSolutionRequest req = new SearchQuizSolutionRequest();
    	req.setPageNumber(0);
    	
    	SearchQuizSolutionResponse res = doPost("/intranet/quiz-solution/search-by-participant", user3Token, req, SearchQuizSolutionResponse.class);
    	assertThat(res.getItems().size(), equalTo(1));
    	
    	QuizSolutionItem item = res.getItems().get(0);
        assertThat(item.getQuiz().getUuid(), equalTo(publishedQuizUuid));
        
        // ---------------- User#1 searches for solutions of his published quizzes ----------------------
    	res = doPost("/intranet/quiz-solution/search-by-publisher", user1Token, req, SearchQuizSolutionResponse.class);
    	assertThat(res.getItems().size(), equalTo(1));
    	
    	item = res.getItems().get(0);
        assertThat(item.getQuiz().getUuid(), equalTo(publishedQuizUuid));
        
        
        
        
        // ---------------- User#3 searches all quizzes ----------------------
    	SearchQuizRequest searchQuizRequest = new SearchQuizRequest();
    	searchQuizRequest.setPageNumber(0);
    	
    	SearchQuizResponse searchQuizResponse = doPost("/intranet/quiz/search", user3Token, searchQuizRequest, SearchQuizResponse.class);
    	assertThat(searchQuizResponse.getItems().size(), equalTo(2));
    	
    	QuizItem quizItem = searchQuizResponse.getItems().get(0);
        assertThat(quizItem.getUuid(), notNullValue());
    }
    
    private Triple<UUID, UUID, UUID> submitQuiz(String userToken, String quizTitle) throws Exception {
        // ---------------- User creates Quiz ----------------------
        CreateQuizRequest createQuizRequest = prepareCreateQuizRequest(quizTitle);
        QuizModel quizModel = doPost("/intranet/quiz", userToken, createQuizRequest, QuizModel.class);
        
        // ---------------- User adds Question#1 ----------------------
        CreateQuestionRequest createQuestion1Request = prepareCreateQuestionRequest(QuestionType.SINGLE_CORRECT, ques1MoonIsStar, Arrays.asList(
                Pair.of(yes, true),
                Pair.of(no, false)
        ));
        QuestionDetailModel createdQuestion1DetailModel = doPost("/intranet/quiz/" + quizModel.getUuid() + "/question", userToken, createQuestion1Request, QuestionDetailModel.class);
        
        // ---------------- User adds Question#2 ----------------------
        CreateQuestionRequest createQuestion2Request = prepareCreateQuestionRequest(QuestionType.MULTI_CORRECT, quest2TempCanBeMeasuredIn, Arrays.asList(
                Pair.of(kelvin, true),
                Pair.of(fahrenheit, true),
                Pair.of(gram, false),
                Pair.of(celsius, true),
                Pair.of(liters, false)
        ));
        QuestionDetailModel createdQuestion2DetailModel = doPost("/intranet/quiz/" + quizModel.getUuid() + "/question", user1Token, createQuestion2Request, QuestionDetailModel.class);
    
        return Triple.of(quizModel.getUuid(), createdQuestion1DetailModel.getUuid(), createdQuestion2DetailModel.getUuid());
    }
    
    private CreateQuizRequest prepareCreateQuizRequest(String title) {
        CreateQuizRequest req = new CreateQuizRequest();
        req.setTitle(title);
        return req;
    }
    
    private EditQuizRequest prepareEditQuizRequest(String title) {
        EditQuizRequest req = new EditQuizRequest();
        req.setTitle(title);
        return req;
    }
    
    private SignupUserRequest prepareSignupUserRequest(String email, String password, String firstName, String lastName) {
        SignupUserRequest req = new SignupUserRequest();
        
        req.setEmail(email);
        req.setPassword(password);
        req.setFirstName(firstName);
        req.setLastName(lastName);
        
        return req;
    }
    
    private CreateQuestionRequest prepareCreateQuestionRequest(QuestionType questionType, String content, List<Pair<String, Boolean>> answers) {
        CreateQuestionRequest req = new CreateQuestionRequest();
        req.setQuestionType(questionType);
        req.setQuestionText(content);
        
        for(Pair<String, Boolean> ans : answers) {
            CreateAnswerRequest ansReq = new CreateAnswerRequest();
            ansReq.setAnswerText(ans.getLeft());
            ansReq.setCorrect(ans.getRight());
            req.getAnswerRequests().add(ansReq);
        }
        
        return req;
    }
    
    private EditQuestionRequest prepareEditQuestionRequest(QuestionType questionType, String content, List<Pair<String, Boolean>> answers) {
        EditQuestionRequest req = new EditQuestionRequest();
        req.setQuestionType(questionType);
        req.setQuestionText(content);
        
        for(Pair<String, Boolean> ans : answers) {
            CreateAnswerRequest ansReq = new CreateAnswerRequest();
            ansReq.setAnswerText(ans.getLeft());
            ansReq.setCorrect(ans.getRight());
            req.getAnswerRequests().add(ansReq);
        }
        
        return req;
    }
    
    private PublishQuizRequest preparePublishQuizRequest(ZonedDateTime startDate, ZonedDateTime endDate) {
        PublishQuizRequest req = new PublishQuizRequest();
        
        req.setStartDate(startDate);
        req.setEndDate(endDate);
        
        return req;
    }
    
    private static <T> String objToJson(final T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T jsonToObj(final String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private <T> T doGet(String endpoint, String authToken, Class<T> clazz, Map<String, String> additionalHeaderMap) throws Exception {
    	MockHttpServletRequestBuilder reqBuilder = get(endpoint);
        return doHttp(authToken, null, clazz, reqBuilder, status().isOk(), additionalHeaderMap);
    }
    private <T> T doGet(String endpoint, String authToken, Class<T> clazz) throws Exception {
    	return doGet(endpoint, authToken, clazz, null);
    }
    private <T> T doGetError(String endpoint, String authToken, ResultMatcher statusResultMatcher, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(endpoint);
        return doHttp(authToken, null, clazz, reqBuilder, statusResultMatcher, null);
    }

    private <T> T doPost(String endpoint, String authToken, Object req, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = post(endpoint);
        return doHttp(authToken, req, clazz, reqBuilder, status().isOk(), null);
    }
    private <T> T doPostError(String endpoint, String authToken, Object req, ResultMatcher statusResultMatcher, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = post(endpoint);
        return doHttp(authToken, req, clazz, reqBuilder, statusResultMatcher, null);
    }

    private <T> T doPut(String endpoint, String authToken, Object req, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = put(endpoint);
        return doHttp(authToken, req, clazz, reqBuilder, status().isOk(), null);
    }
    private <T> T doPutError(String endpoint, String authToken, Object req, ResultMatcher statusResultMatcher, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = put(endpoint);
        return doHttp(authToken, req, clazz, reqBuilder, statusResultMatcher, null);
    }
    
    private <T> T doDelete(String endpoint, String authToken, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(endpoint);
        return doHttp(authToken, null, clazz, reqBuilder, status().isOk(), null);
    }
    private <T> T doDeleteError(String endpoint, String authToken, ResultMatcher statusResultMatcher, Class<T> clazz) throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(endpoint);
        return doHttp(authToken, null, clazz, reqBuilder, statusResultMatcher, null);
    }

	private <T> T doHttp(String authToken, Object req, Class<T> clazz, MockHttpServletRequestBuilder reqBuilder, ResultMatcher statusResultMatcher, Map<String, String> additionalHeaderMap) throws UnsupportedEncodingException, Exception {		
		if(! StringUtils.isEmpty(authToken)) {
            reqBuilder = reqBuilder.header("Authorization", "Bearer " + authToken);
        }
        if(req != null) {
            reqBuilder = reqBuilder.content(objToJson(req));
        }
        reqBuilder = reqBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if(! MapUtils.isEmpty(additionalHeaderMap)) {
        	for(Entry<String, String> entry : additionalHeaderMap.entrySet()) {
        		reqBuilder = reqBuilder.header(entry.getKey(), entry.getValue());
        	}
        }
        
        String resJson = mockMvc
            .perform(reqBuilder)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(statusResultMatcher).andReturn().getResponse().getContentAsString();
        
        if(clazz == null) {
            return null;
        } else {
            T res = jsonToObj(resJson, clazz);
            return res;
        }
	}

}
