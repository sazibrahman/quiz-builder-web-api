package com.sazibrahman.quizservice.data.model.v1;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuizSolutionResponse {
    
    private List<QuizSolutionItem> items = new ArrayList<>();
	
    private long total = 0;
	
}
