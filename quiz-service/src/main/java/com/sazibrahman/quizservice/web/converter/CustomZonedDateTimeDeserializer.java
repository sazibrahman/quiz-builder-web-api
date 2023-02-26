package com.sazibrahman.quizservice.web.converter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    /**
     * "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     */
	@Override
	public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
		try {
			return ZonedDateTime.parse(p.getText(), CustomZonedDateTimeSerializer.DATE_FORMATTER);
		} catch (DateTimeParseException ex) {
			log.error("LocalDateTime deserialize exception", ex);
			return null;
		}
	}
}