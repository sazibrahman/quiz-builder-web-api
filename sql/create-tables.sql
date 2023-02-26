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


