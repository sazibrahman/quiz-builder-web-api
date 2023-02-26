package com.sazibrahman.quizservice.web.security.jwt.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {

	private String token;
	private boolean forcePasswordChange;
	private int passwordExpiryDayCount;
	
	private Set<String> permissions;
	
	/**
	 * use permissions instead of roles
	 */
	@Deprecated
	private Set<String> roles;
	
}
