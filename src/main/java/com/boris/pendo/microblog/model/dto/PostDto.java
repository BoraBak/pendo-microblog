package com.boris.pendo.microblog.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {

    private String id;

    private String userId;

    private String text;

    private int score;
}
