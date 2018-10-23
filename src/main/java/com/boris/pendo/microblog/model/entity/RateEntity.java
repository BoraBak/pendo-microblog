package com.boris.pendo.microblog.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RateEntity {

//    private String id;

    private String postId;

    private String userId;

    private boolean isRated;
}
