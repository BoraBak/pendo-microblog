package com.boris.pendo.microblog.service;

import com.boris.pendo.microblog.model.dto.PostDto;
import com.boris.pendo.microblog.model.entity.PostEntity;

import java.util.Map;

public interface IPostService {

    PostEntity convertDtoToEntity(PostDto postDto);

    boolean createNewPost(PostEntity postEntity, String eventId);

    boolean updatePost(PostEntity postEntity, String eventId);

    boolean ratePost(PostEntity postEntity, String eventId);

    Map<PostEntity, Double> getTopPosts(String eventId);
}
