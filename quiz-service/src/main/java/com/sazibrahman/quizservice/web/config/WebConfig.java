package com.sazibrahman.quizservice.web.config;

import java.util.Arrays;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
 

@Configuration
@EnableScheduling
@EnableAsync
@Import( {WebSecurityConfig.class} )
@ComponentScan({ "com.sazibrahman.quizservice" })
@EntityScan("com.sazibrahman.quizservice")
@EnableJpaRepositories("com.sazibrahman.quizservice")
@EnableAutoConfiguration
public class WebConfig {
	
    /**
     * By default, the Spring Security Authentication is bound to a ThreadLocal – so, when the 
     * execution flow runs in a new thread with @Async, that's not going to be an authenticated context.
     * https://www.baeldung.com/spring-security-async-principal-propagation
     * 
     * TODO
     * We need to revert back to default mode (SecurityContextHolder.MODE_THREADLOCAL)
     * We use it only for data migration. After the migration, we will revert back.
     */
    @Bean
    public InitializingBean initializingBean() {
      return () -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
    
	@Bean
	public RestTemplate restTemplate() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(new ObjectMapper());
		converter.setSupportedMediaTypes(Arrays.asList(new MediaType("application","pdf")));

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(converter);
		
		return restTemplate;
	}
	
	  @Bean
	    public CorsFilter corsFilter() {
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        CorsConfiguration config = new CorsConfiguration();
	        config.setAllowCredentials(true);
	        config.addAllowedOrigin("*");
	        config.addAllowedHeader("*");
	        config.addAllowedMethod("OPTIONS");
	        config.addAllowedMethod("GET");
	        config.addAllowedMethod("POST");
	        config.addAllowedMethod("PUT");
	        config.addAllowedMethod("DELETE");
	        source.registerCorsConfiguration("/**", config);
	        return new CorsFilter(source);
	    }
}
