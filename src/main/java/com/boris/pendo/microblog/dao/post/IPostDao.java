package com.boris.pendo.microblog.dao.post;

import com.boris.pendo.microblog.model.entity.PostEntity;
import com.boris.pendo.microblog.model.entity.RateEntity;

import java.util.List;

public interface IPostDao {
    void savePost(PostEntity postEntity, String eventId);

    void saveRate(RateEntity rateEntity, String eventId);

    boolean updatePost(PostEntity postEntity, String eventId);

    boolean isRated(PostEntity postEntity);

    boolean ratePost(PostEntity postEntity, String eventId);

    boolean updateIsRated(PostEntity postEntity, String eventId);

    List<PostEntity> getAll();
}
