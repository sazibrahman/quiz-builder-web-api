package com.sazibrahman.quizservice.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class NotFoundException extends Exception {
    
    private static final long serialVersionUID = -8340969640373326162L;

    public NotFoundException(final String msg) {
        super(msg);
    }

}