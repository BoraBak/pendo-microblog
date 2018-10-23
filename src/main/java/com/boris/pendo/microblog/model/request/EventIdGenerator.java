package com.boris.pendo.microblog.model.request;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventIdGenerator {

    @Getter
    @Value("${eventid.string.size:32}")
    private Integer mexTokenLength;

    public String generate() {
        String timePostfix = generateTimestampPartOfId();
        String id = generateRandomPartOfId(timePostfix.length());

        return id + timePostfix;
    }

    private String generateTimestampPartOfId() {
        return Long.toString(System.currentTimeMillis());
    }

    private String generateRandomPartOfId(int timePostfixLength) {
        int finalSizeToGenerate = mexTokenLength - timePostfixLength;

        if (finalSizeToGenerate > 0) {
            return RandomStringUtils.randomAlphanumeric(finalSizeToGenerate);
        }

        return "";
    }

}

