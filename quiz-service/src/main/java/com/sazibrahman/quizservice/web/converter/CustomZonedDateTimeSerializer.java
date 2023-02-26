package com.sazibrahman.quizservice.web.converter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
	
	/**
	 * "MM/dd/yyyy HH:mm:ss"
	 */
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@Override
	public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		try {
			String s = value.format(DATE_FORMATTER);
			gen.writeString(s);
		} catch (DateTimeParseException ex) {
			log.error("LocalDateTime serialize exception", ex);
			gen.writeString((String) null);
		}
	}
}