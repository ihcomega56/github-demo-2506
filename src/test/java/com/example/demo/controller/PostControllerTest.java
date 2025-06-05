package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDraft_shouldReturnCreatedPost() throws Exception {
        // given
        String content = "Test content";
        Post createdPost = new Post(content);
        createdPost.setId(1L);

        when(postService.createDraft(content)).thenReturn(createdPost);

        // when & then
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"" + content + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.draft").value(true));
    }

    @Test
    void createDraft_shouldReturnBadRequestWhenContentIsNull() throws Exception {
        // when & then
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishPost_shouldReturnPublishedPost() throws Exception {
        // given
        Long postId = 1L;
        Post publishedPost = new Post("Content");
        publishedPost.setId(postId);
        publishedPost.setDraft(false);

        when(postService.publishPost(postId)).thenReturn(publishedPost);

        // when & then
        mockMvc.perform(put("/api/posts/drafts/{id}/publish", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.draft").value(false));
    }

    @Test
    void publishPost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given
        Long postId = 999L;
        when(postService.publishPost(postId)).thenReturn(null);

        // when & then
        mockMvc.perform(put("/api/posts/drafts/{id}/publish", postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPost_shouldReturnExistingPost() throws Exception {
        // given
        Long postId = 1L;
        Post post = new Post("Content");
        post.setId(postId);

        when(postService.getPost(postId)).thenReturn(post);

        // when & then
        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    @Test
    void getPost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given
        Long postId = 999L;
        when(postService.getPost(postId)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_shouldReturnNoContentWhenPostExists() throws Exception {
        // given
        Long postId = 1L;
        when(postService.deletePost(postId)).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given
        Long postId = 999L;
        when(postService.deletePost(postId)).thenReturn(false);

        // when & then
        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPublishedPosts_shouldReturnListOfPublishedPosts() throws Exception {
        // given
        Post post1 = new Post("Content 1");
        post1.setId(1L);
        post1.setDraft(false);
        Post post2 = new Post("Content 2");
        post2.setId(2L);
        post2.setDraft(false);
        List<Post> publishedPosts = Arrays.asList(post1, post2);

        when(postService.getAllPublishedPosts()).thenReturn(publishedPosts);

        // when & then
        mockMvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getAllDraftPosts_shouldReturnListOfDraftPosts() throws Exception {
        // given
        Post draft1 = new Post("Draft 1");
        draft1.setId(1L);
        Post draft2 = new Post("Draft 2");
        draft2.setId(2L);
        List<Post> draftPosts = Arrays.asList(draft1, draft2);

        when(postService.getAllDraftPosts()).thenReturn(draftPosts);

        // when & then
        mockMvc.perform(get("/api/posts/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].draft").value(true))
                .andExpect(jsonPath("$[1].draft").value(true));
    }
}