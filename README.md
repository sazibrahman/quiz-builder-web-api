# Simple Quiz Builder REST API
A simple RESTful API (scalable microservice by design) that allows authenticated users to create their own quizzes and other users to take them and see how many questions they got right. It is expected that certain quizzes can have 100K+ visitors completing them.

## Author
- [Saifur Rahman](https://www.linkedin.com/in/sazib)

## Table of contents
* [Functional requirements](#functional-requirements)
* [Score calculation](#score-calculation)
* [Technologies](#technologies)
* [Prerequisites](#prerequisites)
* [Developing Steps](#developing-steps)
   + [Step 1. Preparing ER-Diagram](#)
   + [Step 2. Creating microservice using Spring Boot](#step-2-creating-microservice-using-spring-boot)
   + [Step 3. Creating the Dockerfile for quiz-service](#step-3-creating-the-dockerfile-for-quiz-service)
   + [Step 4. Creating docker-compose.yml](#step-4-creating-docker-compose-yml)
   + [Step 5. Testing application locally](#step-5-testing-application-locally)
* [Building and deploying](#building-and-deploying)
   + [Step 1. Clone the application from git](#step-1-clone-the-application-from-git)
   + [Step 2. Create a JAR file for Quiz-Service](#step-2-create-a-jar-file-for-quiz-service)
   + [Step 3. Start MySql, and Quiz-Service using docker-compose](#step-3-start-mysql-and-quiz-service-using-docker-compose)
   + [Step 4. Configure MySql tables](#step-4-configure-mysql-tables)
* [Accessing Application](#accessing-application)
* [Test Cases](#test-cases)
* [Further Improvement](#further-improvement-ideas)

## Functional requirements 
 * All API calls (other than authentication endpoints themselves) need to be authenticated. 
 * Every user has the ability to create a quiz. 
 * A quiz consists of a quiz title and a list of questions. 
 * Every question contains question text and a list of possible answers. 
 * Questions can be either with a single or multiple correct answers. It should not be possible to submit multiple answers to a question that has only one correct answer. 
 * Quiz can only be taken by other users once published. 
 * Every correctly selected answer adds a positive weight, and every wrong selected answer reduces it. 
 * Unpublished quizzes can be updated by the author, but can’t be taken by other visitors. 
 * Published quizzes can’t be edited anymore by the author, only deleted. 
 * Other users can take published quizzes and submit their solutions. 
 * Users should be able to access the list of all the solutions they have submitted to various quizzes. 
 * Users should also be able to see the solutions of other users to their own quizzes so they can get the statistics of how people perform on their quiz. 

## Score calculation
#### Example of a single correct answer question: 
Question: Earth is round (Yes / No)

Explanation:
 * If the question is answered correctly, it is scored as 1 point. 
 * If it is skipped, it is scored as 0 points. 
 * If it is answered, but incorrectly, then it is counted as -1 point in the test. 

#### Example of a multiple correct answers question
Question: You can pay online using (Visa / Mastercard / Greencard / Discover / Statecard) 

Explanation:
 * Correctness weights for right options is 0.333, as there are 3 correct answers. 
 * Correctness weights for wrong options is 0.5, as there are 2 incorrect answers. 
 * If selected: Visa + Mastercard, the question can be considered: 0.333 +  0.333 = 0.666 points. 
 * If selected: Visa + Mastercard + Discover, the question can be considered fully correct = 1 point. 
 * If selected: Visa + Mastercard + Greencard, the question can be considered: 0.333 +  0.333 - 0.5 = 0.166 points correct (16%).
 * If selected all: Visa + Mastercard + Greencard + Discover + Statecard, it is considered: 0.333 + 0.333 + 0.333 – 0.5 - 0.5 = 0% correct. 
 * If selected: Greencard + Statecard, it can be considered: -0.5 - 0.5 = -1 (-100%) correct.

## Technologies
This project is created using the following technologies:
1. Java 8
2. Maven Dependency Management
3. Spring Boot Microservices development:
   + Spring Web
   + Spring Mobile
   + Spring Security with JWT (JSON Web Token)
   + Spring Data JPA with Hibernate implementation
   + Spring Devtools
   + Spring Actuator
   + Spring MockMVC with Junit/Jupiter/Mockito
   + Spring-MockMvc with Testcontainers integration 
4. MySql database
5. Docker

## Prerequisites

You need to install the following software:
 * Java JDK 1.8+
 * SpringBoot 2.4.0+
 * MySql 8+
 * Maven 3.6.3+
 * Git client
 * Docker Compose

## Developing Steps

The following steps to be taken in order to create working microservice-based system using Spring boot and Docker:

**Step 1. Preparing ER-Diagram**

As per the Quiz requirements here are the entities needed:
 * users
 * quiz
 * question
 * answer
 * quiz_attempt
 * question_attempt
 * answer_attempt

![created images](images/er-diagram.png)

**Step 2. Creating microservice using Spring Boot**

Create a maven project “quiz-builder-web-api” under which add “quiz-service” as module. “quiz-service” will be consist of Java code that will eventually be compiled to a jar file.

![created images](images/project-layout.png)

Please feel free to checkout the code from git:

```bash
git clone git@github.com:sazibrahman/quiz-builder-web-api.git
```

**Step 3. Creating the Dockerfile for quiz-service**

Docker file is a list of commands that we want the docker engine to execute. Go to “quiz-service” module and create a Dockerfile:

```bash
FROM openjdk:8
ADD ./target/quiz-service-1.0.0-SNAPSHOT.jar /quiz-service.jar
ENTRYPOINT ["java", "-jar", "quiz-service.jar"]
EXPOSE 8080 3306
```

**Step 4. Creating docker-compose.yml**

Create a docker-compose.yml file under the main project. Add the following docker services.

```bash
version: "3"
services:
  quiz-service-dev-001:
    build: ./quiz-service
    image: quiz-service
    container_name: quiz-service-dev-001
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://quiz-builder-mysql-dev:3306/quiz_builder
    ports:
      - 8080:8080
    restart: always
    networks:
      - quiz-mysql
    depends_on:
      - quiz-builder-mysql-dev

  quiz-service-dev-002:
    build: ./quiz-service
    image: quiz-service
    container_name: quiz-service-dev-002
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://quiz-builder-mysql-dev:3306/quiz_builder
    ports:
      - 8081:8080
    restart: always
    networks:
      - quiz-mysql
    depends_on:
      - quiz-builder-mysql-dev

  quiz-builder-mysql-dev:
    image: mysql:8.0
    container_name: quiz-builder-mysql-dev
    environment:
      - MYSQL_ROOT_PASSWORD=SSDAD6FSA6GSAZDFSA
      - MYSQL_USER=quiz_builder_user
      - MYSQL_PASSWORD=SSDAD6FSA6GSAZDFSA
      - MYSQL_DATABASE=quiz_builder
    ports:
      - 3306:3306
    restart: always
    networks:
      - quiz-mysql
networks:
  quiz-mysql:
```

Also have a look at docker-compose-scale.yml file to scale up more services.

```bash
version: "3"

services:
  quiz-service-dev:
    build: ./quiz-service
    image: quiz-service
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://quiz-builder-mysql-dev:3306/quiz_builder
    ports:
      - "8080-8090:8080"
    networks:
      - quiz-mysql
    depends_on:
      - quiz-builder-mysql-dev
    scale: 3
  
  quiz-builder-mysql-dev:
    image: mysql:8.0
    container_name: quiz-builder-mysql-dev
    environment:
      - MYSQL_ROOT_PASSWORD=SSDAD6FSA6GSAZDFSA
      - MYSQL_USER=quiz_builder_user
      - MYSQL_PASSWORD=SSDAD6FSA6GSAZDFSA
      - MYSQL_DATABASE=quiz_builder
    ports:
      - 3306:3306
    restart: always
    networks:
      - quiz-mysql

networks:
  quiz-mysql:
    driver: bridge
```

![created images](images/docker-service-diagram.png)


**Step 5. Testing application locally**

You can execute unit/integration tests using the following maven command. 

```bash
mvn clean install
```

Every time you run integration tests, Testcontainers library will automatically create and start a “testcontainers/ryuk” container to Docker. “Testcontainers” is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container. Once the tests gets executed, “testcontainers/ryuk” container will be removed from Docker.

![created images](images/docker-images.png)


## Building and deploying

**Step 1. Create a JAR file for Quiz-Service**

Run maven command to build project jar. Jar file gets created under “./quiz-service /target/quiz-service-1.0.0-SNAPSHOT.jar” :

```bash
mvn clean package
```

**Step 2. Start MySql, and Quiz-Service using docker-compose**

Use this docket compose command to deploy and run your entire services. This will stand up one instance of MySql database and two instances of quiz-service.

```bash
docker-compose -f docker-compose.yml up -d
```

If you want to scale more service instances, use this command:

```bash
docker-compose -f docker-compose-scale.yml up -d
```

![created images](images/docker-containers.png)

**Step 3. Configure MySql tables**

Add the database tables to MySql as described into "/sql/create-tables.sql(./sql/create-tables.sql)" file:

```bash
CREATE TABLE users (
  uuid binary(16) NOT NULL,
  created_date datetime DEFAULT NULL,
  deleted_date datetime DEFAULT NULL,
  disabled_date datetime DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  first_name varchar(255) DEFAULT NULL,
  force_password_change_date datetime DEFAULT NULL,
  last_name varchar(255) DEFAULT NULL,
  last_password_reset_date datetime DEFAULT NULL,
  lock_date datetime DEFAULT NULL,
  login_date datetime DEFAULT NULL,
  login_failed_count int DEFAULT NULL,
  middle_name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  password_expiry_date datetime DEFAULT NULL,
  PRIMARY KEY (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE quiz (
  uuid binary(16) NOT NULL,
  created_date datetime NOT NULL,
  deleted_date datetime DEFAULT NULL,
  end_date datetime DEFAULT NULL,
  start_date datetime DEFAULT NULL,
  title varchar(255) DEFAULT NULL,
  created_by_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_quiz_created_by_user (created_by_uuid),
  CONSTRAINT FK_quiz_created_by_user FOREIGN KEY (created_by_uuid) REFERENCES users (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE question (
  uuid binary(16) NOT NULL,
  question_text varchar(255) DEFAULT NULL,
  question_type varchar(255) NOT NULL,
  created_by_uuid binary(16) DEFAULT NULL,
  quiz_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_question_created_by_user (created_by_uuid),
  KEY FK_question_quiz (quiz_uuid),
  CONSTRAINT FK_question_quiz FOREIGN KEY (quiz_uuid) REFERENCES quiz (uuid),
  CONSTRAINT FK_question_created_by_user FOREIGN KEY (created_by_uuid) REFERENCES users (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE answer (
  uuid binary(16) NOT NULL,
  answer_text varchar(255) DEFAULT NULL,
  correct bit(1) NOT NULL,
  question_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_answer_question (question_uuid),
  CONSTRAINT FK_answer_question FOREIGN KEY (question_uuid) REFERENCES question (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE quiz_attempt (
  uuid binary(16) NOT NULL,
  correct_percent double NOT NULL,
  created_date datetime NOT NULL,
  end_date datetime DEFAULT NULL,
  score double NOT NULL,
  attempted_by_uuid binary(16) DEFAULT NULL,
  quiz_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_quiz_attempt_attempted_by_user (attempted_by_uuid),
  KEY FK_quiz_attempt_quiz (quiz_uuid),
  CONSTRAINT FK_quiz_attempt_quiz FOREIGN KEY (quiz_uuid) REFERENCES quiz (uuid),
  CONSTRAINT FK_quiz_attempt_attempted_by_user FOREIGN KEY (attempted_by_uuid) REFERENCES users (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE question_attempt (
  uuid binary(16) NOT NULL,
  correct_percent double NOT NULL,
  created_date datetime NOT NULL,
  score double NOT NULL,
  skipped bit(1) NOT NULL,
  question_uuid binary(16) DEFAULT NULL,
  quiz_attempt_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_question_attempt_question (question_uuid),
  KEY FK_question_attempt_quiz_attempt (quiz_attempt_uuid),
  CONSTRAINT FK_question_attempt_question FOREIGN KEY (question_uuid) REFERENCES question (uuid),
  CONSTRAINT FK_question_attempt_quiz_attempt FOREIGN KEY (quiz_attempt_uuid) REFERENCES quiz_attempt (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE answer_attempt (
  uuid binary(16) NOT NULL,
  answer_uuid binary(16) DEFAULT NULL,
  question_attempt_uuid binary(16) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY FK_answer_attempt_answer (answer_uuid),
  KEY FK_answer_attempt_question_attempt (question_attempt_uuid),
  CONSTRAINT FK_answer_attempt_answer FOREIGN KEY (answer_uuid) REFERENCES answer (uuid),
  CONSTRAINT FK_answer_attempt_question_attempt FOREIGN KEY (question_attempt_uuid) REFERENCES question_attempt (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## Accessing Application

Use the following credential to connection to MySql database:

```bash
server: quiz-builder-mysql-dev
port: 3306
schema: quiz_builder
username: quiz_builder_user
password: SSDAD6FSA6GSAZDFSA
```

Two instances of “quiz-service” will start on port 8080 and 8081.

```bash
http://localhost:8081
http://localhost:8082
```

Here are the API Endpoints:

* Sign up User:

```bash
POST /sign-up
Header: [Content-Type: "application/json;charset=UTF-8"]
Request: {"email":"sazib.rahman1@gmail.com","password":"Passw0rd!","firstName":"Saifur","middleName":null,"lastName":"Rahman"}
Response: {"uuid":"b659794a-97cb-44ec-81c9-b5dd12666f54","email":"sazib.rahman1@gmail.com","firstName":"Saifur","middleName":null,"lastName":"Rahman"}
```

* Sign in User

```bash
POST /auth
Header: [Content-Type: "application/json;charset=UTF-8"]
Request: {"username":"sazib.rahman1@gmail.com","password":"Passw0rd!"}
Response: {"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........":false,"passwordExpiryDayCount":5}
```
* Initialize Quiz

```bash
POST /intranet/quiz
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"title":"PublishedQuiz"}
Response: {"uuid":"708ffd1d-29a6-4970-b4ef-b5d0dfde55bb","title":"PublishedQuiz","createdDate":"2022-09-15T00:53:20.887-0500","startDate":null,"endDate":null}
```
* Edit Quiz by quizUuid

```bash
PUT /intranet/quiz/{quizUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"title":"UnpublishedQuiz - Updated Title"}
Response: {"uuid":"691fa5c5-ed30-43a9-a5fe-aac0e27f5042","title":"UnpublishedQuiz - Updated Title","createdDate":"2022-09-15T05:53:21.000+0000","startDate":null,"endDate":null}
```
* Publish Quiz by quizUuid

```bash
PUT /intranet/quiz/{quizUuid}/publish
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"startDate":"2022-09-15T00:53:21.186-0500","endDate":null}
Response: {"uuid":"708ffd1d-29a6-4970-b4ef-b5d0dfde55bb","title":"PublishedQuiz","createdDate":"2022-09-15T05:53:21.000+0000","startDate":"2022-09-15T00:53:21.186-0500","endDate":null}
```
* Delete Quiz by quizUuid

```bash
DELETE /intranet/quiz/{quizUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: N/A
```
* Get Quiz by quizUuid

```bash
GET /intranet/quiz/{quizUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"uuid":"691fa5c5-ed30-43a9-a5fe-aac0e27f5042","title":"UnpublishedQuiz","createdDate":"2022-09-15T05:53:21.000+0000","startDate":null,"endDate":null}
```
* Add Question to Quiz

```bash
POST /intranet/quiz/{quizUuid}/question
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"questionType":"SINGLE_CORRECT","questionText":"Moon is a star","answerRequests":[{"answerText":"Yes","correct":true},{"answerText":"No","correct":false}]}
Response: {"uuid":"f962e263-54f7-45b9-a515-83e3806dc04e","questionType":"SINGLE_CORRECT","questionText":"Moon is a star","answerDetails":[{"uuid":"dd51281e-e8b5-48be-8604-66534583a827","answerText":"Yes","correct":false},{"uuid":"d27e2721-b117-4267-a931-d726429152d3","answerText":"No","correct":false}]}
```
* Edit Question by questionUuid

```bash
PUT /intranet/question/{questionUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"questionType":"MULTI_CORRECT","questionText":"What are planets?","answerRequests":[{"answerText":"Earth","correct":true},{"answerText":"Moon","correct":false},{"answerText":"Sun","correct":false},{"answerText":"Mars","correct":true}]}
Response: {"uuid":"9e5a13f8-9027-4117-a2d4-226f4fb6124d","questionType":"MULTI_CORRECT","questionText":"What are planets?","answerDetails":[{"uuid":"5df94871-9d4f-4ac1-808a-3c204752df2b","answerText":"Earth","correct":false},{"uuid":"1b4d65c1-8170-4fb3-a2d6-094cf53d47e4","answerText":"Moon","correct":false},{"uuid":"614b4b15-d225-43ad-a098-be1d078d3b49","answerText":"Sun","correct":false},{"uuid":"9393ab7c-e186-46d9-9d68-50a004687f1f","answerText":"Mars","correct":false}]}
```
* Delete Question by questionUuid

```bash
DELETE /intranet/question/{questionUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: N/A
```
* Get Question by questionUuid

```bash
GET /intranet/question/{questionUuid}
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"questionType":"SINGLE_CORRECT","questionText":"You love Java","answerRequests":[{"answerText":"Yes","correct":false},{"answerText":"No","correct":true}]}
```
* Take/Attempt Quiz by quizUuid

```bash
POST /intranet/quiz/{quizUuid}/attempt
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"uuid":"ac32db5c-dc09-4009-ba52-235c22d176f7","createdDate":"2022-09-15T00:53:22.535-0500","endDate":null}
```
* Get Next Question(s) to Solve by quizAttemptUuid

```bash
GET /intranet/quiz-attempt/{quizAttemptUuid}/session
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"hasNext":true,"pageSize":1,"questions":[{"uuid":"f2fac176-d984-45b0-a308-2fad33383f82","questionType":"SINGLE_CORRECT","questionText":"Moon is a star","answers":[{"uuid":"363397e0-d13a-4111-aa64-7816c1376eca","answerText":"Yes"},{"uuid":"546bcd8e-4ba0-493b-84bf-3fa096e11707","answerText":"No"}]}]}
```
* Solve Question by questionUuid

```bash
POST /intranet/question/{questionUuid}/attempt
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"skipped":false,"selectedAnswerUuids":["945efb7c-e903-4973-8da8-e5ac316f39cb","8feb2298-940f-458e-b42f-377d285a636d"]}
Response: {"uuid":"c14a17a5-3950-47a6-852c-779e1be24010","questionText":"Temperature can be measured in","createdDate":"2022-09-15T01:11:27.756-0500","skipped":false,"score":-1.0,"correctPercent":-100.0}
```
* Get Quiz Result for Publisher by quizAttemptUuid

```bash
getQuizSessionRecordForPublisher
GET /intranet/quiz-attempt/{quizAttemptUuid}/session-record/publisher
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"quizAttemptUuid":"52bf96d5-d7d7-4fce-82a9-c294e4d1c556","quizTitle":"PublishedQuiz","score":-1.0,"correctPercent":-50.0,"createdDate":"2022-09-15T06:11:27.000+0000","endDate":"2022-09-15T06:11:27.000+0000","questions":[{"uuid":"f2fac176-d984-45b0-a308-2fad33383f82","questionType":"SINGLE_CORRECT","questionText":"Moon is a star","skipped":true,"score":0.0,"correctPercent":0.0,"answers":[{"uuid":"363397e0-d13a-4111-aa64-7816c1376eca","answerText":"Yes","correct":true,"userSelected":false},{"uuid":"546bcd8e-4ba0-493b-84bf-3fa096e11707","answerText":"No","correct":false,"userSelected":false}]},{"uuid":"ad4bc72e-023d-42e0-9372-274ce8f2e851","questionType":"MULTI_CORRECT","questionText":"Temperature can be measured in","skipped":false,"score":-1.0,"correctPercent":-100.0,"answers":[{"uuid":"0c2fc55e-e1e6-4bc6-8a91-a20eaa34258d","answerText":"Celsius","correct":true,"userSelected":false},{"uuid":"5a27d258-80f5-4cff-9cf2-0df7ae3e9874","answerText":"Fahrenheit","correct":true,"userSelected":false},{"uuid":"8feb2298-940f-458e-b42f-377d285a636d","answerText":"Liters","correct":false,"userSelected":true},{"uuid":"945efb7c-e903-4973-8da8-e5ac316f39cb","answerText":"Gram","correct":false,"userSelected":true},{"uuid":"c1ec8909-56f8-48db-ae07-dc5dfc5a981d","answerText":"Kelvin","correct":true,"userSelected":false}]}]}
```
* Get Quiz Result for Participant by quizAttemptUuid

```bash
GET /intranet/quiz-attempt/{quizAttemptUuid}/session-record/participant
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: N/A
Response: {"quizAttemptUuid":"7e14f2f9-2901-4c21-90cc-6654eb92486d","quizTitle":"PublishedQuiz","score":-1.0,"correctPercent":-50.0,"createdDate":"2022-09-15T06:11:28.000+0000","endDate":"2022-09-15T06:11:28.000+0000","questions":[{"uuid":"f2fac176-d984-45b0-a308-2fad33383f82","questionType":"SINGLE_CORRECT","questionText":"Moon is a star","skipped":true,"score":0.0,"correctPercent":0.0,"answers":[{"uuid":"363397e0-d13a-4111-aa64-7816c1376eca","answerText":"Yes"},{"uuid":"546bcd8e-4ba0-493b-84bf-3fa096e11707","answerText":"No"}]},{"uuid":"ad4bc72e-023d-42e0-9372-274ce8f2e851","questionType":"MULTI_CORRECT","questionText":"Temperature can be measured in","skipped":false,"score":-1.0,"correctPercent":-100.0,"answers":[{"uuid":"0c2fc55e-e1e6-4bc6-8a91-a20eaa34258d","answerText":"Celsius"},{"uuid":"5a27d258-80f5-4cff-9cf2-0df7ae3e9874","answerText":"Fahrenheit"},{"uuid":"8feb2298-940f-458e-b42f-377d285a636d","answerText":"Liters"},{"uuid":"945efb7c-e903-4973-8da8-e5ac316f39cb","answerText":"Gram"},{"uuid":"c1ec8909-56f8-48db-ae07-dc5dfc5a981d","answerText":"Kelvin"}]}]}
```
* List All Quizzes for Logged-in User

```bash
POST /intranet/quiz/search
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: 
Response: 
```
* List Results of All Quizzes Published by Logged-in User

```bash
POST /intranet/quiz-solution/search-by-publisher
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"pageNumber":0}
Response: {"items":[{"quiz":{"uuid":"7e4a2e15-229c-4355-b194-75506a16bd9a","title":"PublishedQuiz","createdByFullName":"Saifur Rahman","createdDate":"2022-09-15T06:11:25.000+0000","startDate":"2022-09-15T06:11:25.000+0000","endDate":null},"solution":{"uuid":"52bf96d5-d7d7-4fce-82a9-c294e4d1c556","attemptedByFullName":"Saifur Rahman","createdDate":"2022-09-15T06:11:27.000+0000","endDate":"2022-09-15T06:11:27.000+0000","score":-1.0,"correctPercent":-50.0}}],"total":1}
```
* List Results of All Quizzes Solved by Logged-in User

```bash
POST /intranet/quiz-solution/search-by-participant
Header: [Content-Type: "application/json;charset=UTF-8", Authorization: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXppYi5y........"]
Request: {"pageNumber":0}
Response: {"items":[{"quiz":{"uuid":"7e4a2e15-229c-4355-b194-75506a16bd9a","title":"PublishedQuiz","createdByFullName":"Saifur Rahman","createdDate":"2022-09-15T06:11:25.000+0000","startDate":"2022-09-15T06:11:25.000+0000","endDate":null},"solution":{"uuid":"52bf96d5-d7d7-4fce-82a9-c294e4d1c556","attemptedByFullName":"Saifur Rahman","createdDate":"2022-09-15T06:11:27.000+0000","endDate":"2022-09-15T06:11:27.000+0000","score":-1.0,"correctPercent":-50.0}}],"total":1}
```

## Test Cases

**TestCase#01 - Initialize:**

```bash
 * User#1 signs up  
 * User#1 logs in  
 * User#1 creates "PublishedQuiz", adds Question, publishes Quiz 
 * User#1 creates "UnpublishedQuiz", adds Question 
 * User#1 creates "DeletedQuiz", adds Question, deletes Quiz --
 * User#2 signs up  
 * User#2 logs in  
 * User#3 signs up  
 * User#3 logs in  
```

**TestCase#02 - Invalid sign up:**

```bash
 * User#1 fails to sign up with the same creds
```

**TestCase#03 - Invalid log in:**

```bash
 * Some invalid log in request fails due to invalid creds
```

**TestCase#04 - User#1 fails to modify "PublishedQuiz":**

```bash
 * User#1 gets PublishedQuiz 
 * User#1 fails to edit PublishedQuiz 
 * User#1 gets Question 
 * User#1 fails to add Question to Quiz#1 
 * User#1 fails to delete Question of Quiz#1 
 * User#1 fails to edit Question of Quiz#1 
```

**TestCase#05 User#1 fails to modify "DeletedQuiz":**

```bash
 * User#1 fails to get DeletedQuiz 
 * User#1 fails edit DeletedQuiz 
 * User#1 fails to get Question of DeletedQuiz 
 * User#1 fails to add Question to DeletedQuiz 
 * User#1 fails to delete Question of DeletedQuiz 
 * User#1 fails to edit Question of DeletedQuiz 
```

**TestCase#06 User#1 modifies "UnpublishedQuiz":**

```bash
 * User#1 gets PublishedQuiz 
 * User#1 edits UnpublishedQuiz 
 * User#1 adds Question to UnpublishedQuiz 
 * User#1 edits Question 
 * User#1 deletes Question 
```

**TestCase#07 User#2 fails to modify "UnpublishedQuiz":**

```bash
 * User#2 fails to get Quiz#1 
 * User#2 fails edit Quiz#1 
 * User#2 fails to delete Quiz#1 
 * User#2 fails to get Question of Quiz#1  
 * User#2 fails to add Question to Quiz#1 
 * User#2 fails to delete Question of Quiz#1 
 * User#2 fails to edit Question of Quiz#1 
```

**TestCase#08 User#2 fails to attempt UnpublishedQuiz:**

```bash
 * User#2 fails to attempt UnpublishedQuiz 
```
**TestCase#09 User#2 fails to attempt DeletedQuiz:**

```bash
 * User#2 fails to attempt DeletedQuiz 
```
**TestCase#10 User#2 attempt PublishedQuiz using Mobile:**

```bash
 * User#2 attempts PublishedQuiz 
 * User#2 gets next question 
 * User#2 attempts question 
 * User#2 gets next question 
 * User#2 gets record 
 * User#1 gets full record 
 * User#2 fails to re-attempt PublishedQuiz 
 * User#2 searches for his solutions 
 * User#1 searches for solutions of his published quizzes 
 * User#3 searches all quizzes 
```

**TestCase#11 User#3 attempts PublishedQuiz using Desktop:**

```bash
 * User#3 attempts PublishedQuiz 
 * User#3 gets next question 
 * User#3 attempts question 
 * User#3 gets record 
 * User#1 gets full record 
 * User#3 fails to re-attempt PublishedQuiz 
 * User#3 searches for his solutions 
 * User#1 searches for solutions of his published quizzes 
 * User#3 searches all quizzes 
```


## Further Improvement / Ideas

Here are some ideas to take this service to the next level:
 * Load balancer can be setup in front of "quiz-service"
 * Our "quiz-service" can be split further to multiple microservices. Each microservices can stand alone and can communicate to each other.
 * More views/indexes can be added to DB tables.
 * Chaching can be introduced.

 
 
 
