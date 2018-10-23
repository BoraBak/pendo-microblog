package com.boris.pendo.microblog.util;

import java.time.Instant;
import java.util.Date;

public class InstantConverter {

    public Date convertToDatabaseColumn(Instant attribute) {
        return attribute == null ? null : new Date(attribute.getEpochSecond() * 1000);
    }

    public Instant convertToEntityAttribute(Date dbData) {
        return dbData == null ? null : Instant.ofEpochMilli(dbData.getTime());
    }

}
