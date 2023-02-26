package com.sazibrahman.quizservice.web.controller.v1;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sazibrahman.quizservice.web.security.jwt.JwtTokenUtil;
import com.sazibrahman.quizservice.web.security.jwt.JwtUserAuthenticationProvider;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtAuthenticationRequest;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtAuthenticationResponse;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

@RestController
public class JwtInternalUserAuthenticationRestController extends AbstractBaseController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private JwtUserAuthenticationProvider jwtUserAuthenticationProvider;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<JwtAuthenticationResponse> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
		
		String username = authenticationRequest.getUsername();
		
		// Perform the security
		final Authentication internalAuth = jwtUserAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, authenticationRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(internalAuth);

		JwtUser jwtUser = (JwtUser) internalAuth.getPrincipal();
		
		Date now = new Date();
		
		// generate JWT token
		final String token = jwtTokenUtil.generateToken(jwtUser.getUsername());

		JwtAuthenticationResponse authResponse = new JwtAuthenticationResponse();
		authResponse.setToken(token);
		
		authResponse.setPasswordExpiryDayCount(5);
		
		if(! CollectionUtils.isEmpty(internalAuth.getAuthorities())) {
			authResponse.setPermissions(internalAuth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
		}

		if(jwtUser.getForcePasswordChangeDate() != null && jwtUser.getForcePasswordChangeDate().before(now)) {
			authResponse.setForcePasswordChange(true);
		}

		// Return the token
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(authResponse, headers, HttpStatus.OK);
	}

}