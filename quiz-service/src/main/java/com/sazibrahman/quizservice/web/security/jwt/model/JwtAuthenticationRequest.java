package com.sazibrahman.quizservice.web.security.jwt.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class  JwtAuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String username;
    private String password;

	@Override
	public String toString() {
		return "JwtAuthenticationRequest [username=" + username + ", password="
				+ password + "]";
	}
}
