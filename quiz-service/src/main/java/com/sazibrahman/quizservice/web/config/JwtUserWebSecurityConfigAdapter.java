package com.sazibrahman.quizservice.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sazibrahman.quizservice.web.security.jwt.JwtUserAuthenticationTokenFilter;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtAuthenticationEntryPoint; 

@Order(1)
@Configuration
public class JwtUserWebSecurityConfigAdapter extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtUserAuthenticationTokenFilter jwtUserAuthenticationTokenFilter;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// we don't need CSRF because our token is invulnerable
				.csrf()
				.disable()

				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler)
				.and()

				// don't create session
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()

				// this allows the pre-flight requests
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll()

				// allow anonymous resource requests
				.antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", 
						// swagger resources
						"/**/*.png", "/**/*.ttf", "/webjars/**", "/swagger-resources/**", "/v2/**", "csrf") 
				.permitAll()
				.antMatchers(
				        "/**/auth", 
				        "/**/sign-up"
				 ).permitAll().anyRequest()
				.authenticated();

		// Custom JWT based security filter
		httpSecurity.addFilterBefore(jwtUserAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// disable page caching
		httpSecurity.headers().cacheControl();
	}
}
