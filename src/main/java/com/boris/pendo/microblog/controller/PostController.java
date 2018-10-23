package com.boris.pendo.microblog.controller;

import com.boris.pendo.microblog.exception.ApiBadRequestException;
import com.boris.pendo.microblog.exception.ApiServerException;
import com.boris.pendo.microblog.model.dto.PostDto;
import com.boris.pendo.microblog.model.entity.PostEntity;
import com.boris.pendo.microblog.model.request.EventIdGenerator;
import com.boris.pendo.microblog.service.IPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
@RestController
@RequestMapping(value = "/microblog")
public class PostController {

    @Autowired
    private EventIdGenerator eventIdGenerator;

    @Autowired
    private IPostService postService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody PostDto postDto) {
        String eventId = eventIdGenerator.generate();

        log.info("Received request to create a new post. EventId: {}", eventId);

        try {
            PostEntity postEntity = postService.convertDtoToEntity(postDto);

            postService.createNewPost(postEntity, eventId);
        } catch (ApiBadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ApiServerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Got an error on insert to create new post. EventId {}", eventId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Finished to create a new post. EventId: {}", eventId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestBody PostDto postDto) {
        String eventId = eventIdGenerator.generate();

        log.info("Received request to update a post. EventId: {}", eventId);

        try {
            PostEntity postEntity = postService.convertDtoToEntity(postDto);

            postService.updatePost(postEntity, eventId);
        } catch (ApiBadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ApiServerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Got an error on update a post. EventId {}", eventId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Finished to update a post. EventId: {}", eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/rate", method = RequestMethod.PUT)
    public ResponseEntity<?> rate(@RequestBody PostDto postDto) {
        String eventId = eventIdGenerator.generate();

        log.info("Received request to up-vote/down-vote a post. EventId: {}", eventId);

        try {
            PostEntity postEntity = postService.convertDtoToEntity(postDto);

            postService.ratePost(postEntity, eventId);
        } catch (ApiBadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ApiServerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Got an error on giving a score to a post. EventId {}", eventId, e);
        }

        log.info("Finished to give a score to a post. EventId: {}", eventId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/topPosts", method = RequestMethod.GET)
    public ResponseEntity<?> topPosts() {
        String eventId = eventIdGenerator.generate();

        log.info("Received request to retrieve Top Posts. EventId: {}", eventId);

        Map<PostEntity, Double> topPosts = null;
        try {
            topPosts = postService.getTopPosts(eventId);
        } catch (Exception e) {
            log.error("Got an error retrieving Top Posts. EventId {}", eventId, e);
        }

        log.info("Finished to retrieve Top Posts. EventId: {}", eventId);

        return new ResponseEntity<>(topPosts, HttpStatus.OK);
    }

}
