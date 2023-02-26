package com.sazibrahman.quizservice.util;

import org.apache.commons.lang3.StringUtils;

import com.sazibrahman.quizservice.data.entity.v1.User;

public class NameUtil {
	
	private NameUtil() {
	}
	
	public static String prepareFullName(User user) {
        StringBuilder fullName = new StringBuilder();

        if(StringUtils.isNotBlank(user.getFirstName())) {
            fullName.append(user.getFirstName());
        }

        if(StringUtils.isNotBlank(user.getMiddleName())) {
            if(fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(user.getMiddleName());
        }
        
        if(StringUtils.isNotBlank(user.getLastName())) {
            if(fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(user.getLastName());
        }
        
        return fullName.toString();
    }
    
}
