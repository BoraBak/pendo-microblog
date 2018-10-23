package com.boris.pendo.microblog.service;

import com.boris.pendo.microblog.dao.post.IPostDao;
import com.boris.pendo.microblog.dao.user.IUserDao;
import com.boris.pendo.microblog.exception.ApiBadRequestException;
import com.boris.pendo.microblog.exception.ApiServerException;
import com.boris.pendo.microblog.model.dto.PostDto;
import com.boris.pendo.microblog.model.entity.PostEntity;
import com.boris.pendo.microblog.model.entity.RateEntity;
import com.boris.pendo.microblog.model.request.EventIdGenerator;
import com.boris.pendo.microblog.util.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService implements IPostService {

    @Value("${elements.limit:3}")
    private Integer elementsLimit;

    @Autowired
    private IPostDao postDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private EventIdGenerator eventIdGenerator;

    private Map<PostEntity, Double> topPosts;

    @PostConstruct
    public void init() {
        topPosts = new TreeMap<>();
        topPostsScheduler();
    }

    @Override
    public PostEntity convertDtoToEntity(PostDto postDto) {
        return PostEntity.builder()
                .id(postDto.getId() != null ? postDto.getId() : eventIdGenerator.generate())
                .userId(postDto.getUserId())
                .text(postDto.getText())
                .score(postDto.getScore())
                .build();
    }

    @Override
    @Transactional
    public boolean createNewPost(PostEntity postEntity, String eventId) {

        validateNotNullInputArgs(postEntity.getUserId(), postEntity.getText(), eventId);

        String userId = postEntity.getUserId();
        if (userDao.isUserExist(userId)) {

            postDao.savePost(postEntity, eventId);

            RateEntity rateEntity = RateEntity.builder()
                    .postId(postEntity.getId())
                    .userId(postEntity.getUserId())
                    .isRated(false)
                    .build();
            postDao.saveRate(rateEntity, eventId);
        } else {
            log.info("User {} doesn't exist in DB. EventId {}", userId, eventId);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User " + userId + " doesn't exist in DB. EventId {}", eventId);
        }
        return true;
    }

    @Override
    public boolean updatePost(PostEntity postEntity, String eventId) {

        validateNotNullInputArgs(postEntity.getId(), postEntity.getUserId(), postEntity.getText(), eventId);

        boolean resultUpdatePost = postDao.updatePost(postEntity, eventId);
        if (!resultUpdatePost) {
            log.info("Update post to DB didn't succeeded. EventId {}", eventId);
            throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update post to DB didn't succeeded.", eventId);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean ratePost(PostEntity postEntity, String eventId) {

        validateNotNullInputArgs(postEntity.getId(), postEntity.getUserId(), "tmpIgnore", eventId);

        if (postDao.isRated(postEntity)) {
            log.info("Post {} has been already rated once by user {}. EventId {}", postEntity.getId(), postEntity.getUserId(), eventId);
        } else {
            boolean resultRatePost = postDao.ratePost(postEntity, eventId);
            if (!resultRatePost) {
                log.info("Rate post didn't succeeded. EventId {}", eventId);
                throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Rate post didn't succeeded.", eventId);
            }
            boolean resultUpdateIsRated = postDao.updateIsRated(postEntity, eventId);
            if (!resultUpdateIsRated) {
                log.info("Update of post status to isRated = true didn't succeeded. EventId {}", eventId);
                throw new ApiServerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update of post status to isRated = true didn't succeeded.", eventId);
            }
        }
        return true;
    }

    @Override
    public Map<PostEntity, Double> getTopPosts(String eventId) {
        return topPosts;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void topPostsScheduler() {

        List<PostEntity> posts = postDao.getAll();

        Map<PostEntity, Double> normalizedPostsScores = calculateAndNormalizeScores(posts);

        topPosts = sortAndPickTopScores(normalizedPostsScores);
    }


    private void validateNotNullInputArgs(String id, String userId, String text, String eventId) {
        if (id == null || id.isEmpty()) {
            log.error("PostID is not valid. EventId: {}", eventId);
            throw new ApiBadRequestException("PostID is not valid", eventId);
        }
        validateNotNullInputArgs(userId, text, eventId);
    }

    private void validateNotNullInputArgs(String userId, String text, String eventId) throws ApiBadRequestException {
        if (userId == null) {
            log.error("UserID is null. EventId: {}", eventId);
            throw new ApiBadRequestException("UserID is null.", eventId);
        }
        if (text == null || text.isEmpty()) {
            log.error("Post's text is not valid. EventId: {}", eventId);
            throw new ApiBadRequestException("Post's text is not valid", eventId);
        }
    }

    private Map<PostEntity, Double> calculateAndNormalizeScores(List<PostEntity> posts) {
        return posts.stream().collect(Collectors.toMap(Function.identity(),
                (postEntity -> {
                    long normalizedDate = Calculation.dateNormalizer(postEntity.getCreateTime());
                    double normalizedScore = Calculation.calculateScore(postEntity.getScore(), normalizedDate);
                    return normalizedScore;
                }),
                (v1, v2) -> {
                    throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                }));
    }

    private Map<PostEntity, Double> sortAndPickTopScores(Map<PostEntity, Double> normalizedPostsScores) {
        return normalizedPostsScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(elementsLimit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

}
