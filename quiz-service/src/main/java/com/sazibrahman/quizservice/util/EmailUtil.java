package com.sazibrahman.quizservice.util;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailUtil {

    private EmailUtil() {
    }
    
    public static boolean isValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
    
}
