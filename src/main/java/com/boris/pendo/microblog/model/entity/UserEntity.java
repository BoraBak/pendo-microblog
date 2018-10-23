package com.boris.pendo.microblog.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEntity {

    private String id;

    private String name;
}
