package com.sazibrahman.quizservice.data.model.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;  

@Setter
@Getter
public class CreateQuestionAttemptRequest {

	private boolean skipped;
	private List<UUID> selectedAnswerUuids = new ArrayList<>();
    
}
