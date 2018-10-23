package com.boris.pendo.microblog.dao.user;

import com.boris.pendo.microblog.exception.ApiServerException;
import com.boris.pendo.microblog.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Repository
public class UserDao implements IUserDao {

    private final String IS_USER_EXIST =
            "SELECT count(*) as is_exist " +
                    " FROM user " +
                    " WHERE id = ?;";

    private final String INSERT_INTO_USER =
            "INSERT IGNORE INTO user(id, name) " +
                    " VALUES (?, ?);";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean isUserExist(String userId) {
        return jdbcTemplate.queryForObject(IS_USER_EXIST, new Object[]{userId}, Integer.class) > 0;
    }


    @Override
    public boolean saveUser(UserEntity userEntity, String eventId) {
        boolean result;
        try {
            result = jdbcTemplate.update(INSERT_INTO_USER, ps -> {
                userMapRow(userEntity, ps);
            }) > 0;
        } catch (DataAccessException e) {
            log.error("Got an error on insert to user table. EventId {}", eventId, e);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Got an error on insert to user table.", eventId);
        }
        return result;
    }

    private void userMapRow(UserEntity postEntity, PreparedStatement ps) throws SQLException {
        ps.setString(1, postEntity.getId());
        ps.setString(2, postEntity.getName());
    }
}
