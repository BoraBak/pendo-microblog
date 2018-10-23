package com.boris.pendo.microblog.dao.post;

import com.boris.pendo.microblog.exception.ApiServerException;
import com.boris.pendo.microblog.model.entity.PostEntity;
import com.boris.pendo.microblog.model.entity.RateEntity;
import com.boris.pendo.microblog.util.InstantConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
public class PostDao implements IPostDao {

    private final String INSERT_INTO_POST =
            "INSERT IGNORE INTO post(id, user_id, text, score, create_time) " +
                    " VALUES (?, ?, ?, ?, ?);";

    private final String INSERT_INTO_RATE =
            "INSERT IGNORE INTO rate(is_rated, post_id, user_id) " +
                    " VALUES (?, ?, ?);";

    private final String UPDATE_POST =
            "UPDATE post " +
                    " SET text = ? " +
                    " WHERE id = ? AND user_id = ?;";

    private final String IS_RATED =
            "SELECT is_rated " +
                    " FROM rate " +
                    " WHERE post_id = ? AND user_id = ?;";

    private final String RATE_POST =
            "UPDATE post " +
                    " SET score=?+score " +
                    " WHERE id=? AND user_id = ?;";

    private final String UPDATE_RATED =
            "UPDATE rate " +
                    "SET is_rated = ? " +
                    " WHERE post_id = ? AND user_id = ?;";

    private final String SELECT_ALL_POSTS =
            "SELECT * FROM post;";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private InstantConverter instantConverter = new InstantConverter();


    @Override
    public void savePost(PostEntity postEntity, String eventId) {
        try {
            jdbcTemplate.update(INSERT_INTO_POST, ps -> {
                postMapRow(postEntity, ps);
            });
        } catch (DataAccessException e) {
            log.error("Got an error on insert to post table. EventId {}", eventId, e);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Got an error on insert to post table.", eventId);
        }
    }

    @Override
    public void saveRate(RateEntity rateEntity, String eventId) {
        try {
            jdbcTemplate.update(INSERT_INTO_RATE, ps -> {
                rateMapRow(rateEntity, ps);
            });
        } catch (DataAccessException e) {
            log.error("Got an error on insert to rate table. EventId {}", eventId, e);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Got an error on insert to rate table.", eventId);
        }
    }

    @Override
    public boolean updatePost(PostEntity postEntity, String eventId) {
        boolean result = false;
        try {
            result = jdbcTemplate.update(UPDATE_POST, ps -> {
                updatePostMapRow(postEntity, ps);
            }) > 0;
        } catch (DataAccessException e) {
            log.error("Got an error on update post table. EventId {}", eventId, e);
        }
        return result;

    }

    @Override
    public boolean isRated(PostEntity postEntity) {
        return jdbcTemplate.queryForObject(IS_RATED, new Object[]{postEntity.getId(), postEntity.getUserId()}, Boolean.class);
    }

    @Override
    public boolean ratePost(PostEntity postEntity, String eventId) {
        boolean result;
        try {
            result = jdbcTemplate.update(RATE_POST, ps -> {
                ratePostMapRow(postEntity, ps);
            }) > 0;
        } catch (DataAccessException e) {
            log.error("Got an error on rate post action, on table post. EventId {}", eventId, e);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Got an error on rate post action, on table post.", eventId);
        }
        return result;

    }

    @Override
    public boolean updateIsRated(PostEntity postEntity, String eventId) {
        boolean result;
        try {
            RateEntity rateEntity = RateEntity.builder()
                    .postId(postEntity.getId())
                    .userId(postEntity.getUserId())
                    .isRated(true)
                    .build();
            result = jdbcTemplate.update(UPDATE_RATED, ps -> {
                rateMapRow(rateEntity, ps);
            }) > 0;
        } catch (DataAccessException e) {
            log.error("Got an error on update rate table. EventId {}", eventId, e);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Got an error on update rate table.", eventId);
        }
        return result;

    }

    @Override
    public List<PostEntity> getAll() {
        return jdbcTemplate.query(SELECT_ALL_POSTS, new RowMapper<PostEntity>() {
            @Override
            public PostEntity mapRow(ResultSet rs, int i) throws SQLException {
                return PostEntity.builder()
                        .id(rs.getString("id"))
                        .userId(rs.getString("user_id"))
                        .text(rs.getString("text"))
                        .score(rs.getInt("score"))
                        .createTime(instantConverter.convertToEntityAttribute(rs.getTimestamp("create_time")))
                        .build();
            }
        });
    }

    private void postMapRow(PostEntity postEntity, PreparedStatement ps) throws SQLException {
        ps.setString(1, postEntity.getId());
        ps.setString(2, postEntity.getUserId());
        ps.setString(3, postEntity.getText());
        ps.setInt(4, postEntity.getScore());
        ps.setTimestamp(5, new Timestamp((instantConverter.convertToDatabaseColumn(Instant.now()).getTime())));
    }

    private void rateMapRow(RateEntity rateEntity, PreparedStatement ps) throws SQLException {
        ps.setBoolean(1, rateEntity.isRated());
        ps.setString(2, rateEntity.getPostId());
        ps.setString(3, rateEntity.getUserId());
    }

    private void updatePostMapRow(PostEntity postEntity, PreparedStatement ps) throws SQLException {
        ps.setString(1, postEntity.getText());
        ps.setString(2, postEntity.getId());
        ps.setString(3, postEntity.getUserId());
    }

    private void ratePostMapRow(PostEntity postEntity, PreparedStatement ps) throws SQLException {
        ps.setInt(1, (Integer.compare(postEntity.getScore(), 0)));
        ps.setString(2, postEntity.getId());
        ps.setString(3, postEntity.getUserId());
    }

}
