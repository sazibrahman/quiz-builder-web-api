package com.sazibrahman.quizservice.data.model.v1;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiError {
 
	private HttpStatus statusEnum;
    private Integer status;
    private String statusPhrase;
    private String message;
 
    private ApiError() {
    }
    
    public static ApiError build(HttpStatus statusEnum, String message) {
        ApiError error = new ApiError();
    	
        error.statusEnum = statusEnum;
        if(statusEnum != null) {
        	error.status = statusEnum.value();
        	error.statusPhrase = statusEnum.getReasonPhrase();
        }
        
        error.message = message;
        
        return error;
    }
 
	@JsonIgnore
    public HttpStatus getStatusEnum() {
		return statusEnum;
	}

	public Integer getStatus() {
		return status;
	}

	public String getStatusPhrase() {
		return statusPhrase;
	}

	public String getMessage() {
		return message;
	}

}