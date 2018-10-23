package com.boris.pendo.microblog.dao.user;

import com.boris.pendo.microblog.model.entity.UserEntity;

public interface IUserDao {

    boolean isUserExist(String userId);

    boolean saveUser(UserEntity userEntity, String eventId);
}
