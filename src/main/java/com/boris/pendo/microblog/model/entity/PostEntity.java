package com.boris.pendo.microblog.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PostEntity {

    private String id;

    private String userId;

    private String text;

    private int score;

    private Instant createTime;
}
