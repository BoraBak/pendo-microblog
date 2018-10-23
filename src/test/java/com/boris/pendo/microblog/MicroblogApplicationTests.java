package com.boris.pendo.microblog;

import com.boris.pendo.microblog.dao.user.UserDao;
import com.boris.pendo.microblog.model.dto.PostDto;
import com.boris.pendo.microblog.model.entity.PostEntity;
import com.boris.pendo.microblog.model.entity.UserEntity;
import com.boris.pendo.microblog.service.PostService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MicroblogApplicationTests {

    private final String EVENT_ID = "tes_tEventId_1234";
    private final String POST_ID = "test_PostId_1234";
    private final String USER_ID = "test_UserId_1234";
    private final String USER_NAME = "test_UserName_1234";
    private final String TEXT = "test_Text_1234";
    private final int SCORE = 5;

    @Autowired
    private PostService postService;

    @Autowired
    private UserDao userDao;

    @Before
    public void init() {
        userDao.saveUser(UserEntity.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .build(), EVENT_ID);
    }

    @Test
    public void convertDtoToEntity() {
        PostDto postDto = buildPostDto();

        PostEntity postEntity = postService.convertDtoToEntity(postDto);
        Assert.assertNotNull(postEntity);
    }

    @Test
    public void createPost() {
        PostDto postDto = buildPostDto();

        PostEntity postEntity = postService.convertDtoToEntity(postDto);

        boolean result = postService.createNewPost(postEntity, EVENT_ID);
        Assert.assertTrue(result);
    }

    @Test
    public void updatePost() {
        PostDto postDto = buildPostDto("test_Text_Modified_123456478");

        PostEntity postEntity = postService.convertDtoToEntity(postDto);

        boolean result = postService.updatePost(postEntity, EVENT_ID);
        Assert.assertTrue(result);
    }

    @Test
    public void ratePost() {
        PostDto postDto = buildPostDto();

        PostEntity postEntity = postService.convertDtoToEntity(postDto);

        boolean result = postService.ratePost(postEntity, EVENT_ID);
        Assert.assertTrue(result);
    }

    @Test
    public void getTopPosts() {
        postService.topPostsScheduler();
        Map<PostEntity, Double> topPosts = postService.getTopPosts(EVENT_ID);
        Assert.assertNotNull(topPosts);
    }

    private PostDto buildPostDto(String text) {
        PostDto postDto = buildPostDto();
        postDto.setText(text);
        return postDto;
    }

    private PostDto buildPostDto() {
        return PostDto.builder()
                .id(POST_ID)
                .userId(USER_ID)
                .text(TEXT)
                .score(SCORE)
                .build();
    }

}
