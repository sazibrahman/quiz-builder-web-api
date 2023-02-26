package com.sazibrahman.quizservice.data.entity.converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.AttributeConverter;

/**
 * java -> DB : convert any ZonedDateTime to only UTC LocalDateTime
 * DB -> java : convert UTC LocalDateTime to only UTC LocalDateTime
 */
// @Converter(autoApply = true)
public class ZocalDateTimeToDbUtcConverter implements AttributeConverter<ZonedDateTime, Timestamp> {
    
    private ZoneId utcZoneId = ZoneId.of("UTC");
    
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        if(zonedDateTime == null) {
            return null;
        }
        
        ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameInstant(utcZoneId);
        return Timestamp.from(utcZonedDateTime.toInstant());
    }
 
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp dbUtcLocalDateTime) {
        if(dbUtcLocalDateTime == null) {
            return null;
        }
        
        return ZonedDateTime.ofInstant(dbUtcLocalDateTime.toInstant(), utcZoneId);
    }
}