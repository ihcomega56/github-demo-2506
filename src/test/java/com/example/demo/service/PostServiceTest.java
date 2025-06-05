package com.example.demo.service;

import com.example.demo.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService();
    }

    @Test
    void createDraft_shouldCreatePostWithCorrectContent() {
        // given
        String content = "Test content";

        // when
        Post result = postService.createDraft(content);

        // then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertTrue(result.isDraft());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNull(result.getPublishedAt());
    }

    @Test
    void createDraft_shouldGenerateUniqueIds() {
        // given
        String content1 = "First post";
        String content2 = "Second post";

        // when
        Post post1 = postService.createDraft(content1);
        Post post2 = postService.createDraft(content2);

        // then
        assertNotEquals(post1.getId(), post2.getId());
    }

    @Test
    void publishPost_shouldPublishExistingDraft() {
        // given
        Post draft = postService.createDraft("Draft content");
        Long draftId = draft.getId();

        // when
        Post result = postService.publishPost(draftId);

        // then
        assertNotNull(result);
        assertEquals(draftId, result.getId());
        assertFalse(result.isDraft());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    void publishPost_shouldReturnNullForNonexistentPost() {
        // given
        Long nonexistentId = 999L;

        // when
        Post result = postService.publishPost(nonexistentId);

        // then
        assertNull(result);
    }

    @Test
    void publishPost_shouldReturnNullForAlreadyPublishedPost() {
        // given
        Post draft = postService.createDraft("Draft content");
        Long draftId = draft.getId();
        postService.publishPost(draftId); // publish first time

        // when
        Post result = postService.publishPost(draftId); // try to publish again

        // then
        assertNull(result);
    }

    @Test
    void deletePost_shouldReturnTrueForExistingPost() {
        // given
        Post post = postService.createDraft("Content to delete");
        Long postId = post.getId();

        // when
        boolean result = postService.deletePost(postId);

        // then
        assertTrue(result);
        assertNull(postService.getPost(postId));
    }

    @Test
    void deletePost_shouldReturnFalseForNonexistentPost() {
        // given
        Long nonexistentId = 999L;

        // when
        boolean result = postService.deletePost(nonexistentId);

        // then
        assertFalse(result);
    }

    @Test
    void getPost_shouldReturnExistingPost() {
        // given
        Post originalPost = postService.createDraft("Test content");
        Long postId = originalPost.getId();

        // when
        Post result = postService.getPost(postId);

        // then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Test content", result.getContent());
    }

    @Test
    void getPost_shouldReturnNullForNonexistentPost() {
        // given
        Long nonexistentId = 999L;

        // when
        Post result = postService.getPost(nonexistentId);

        // then
        assertNull(result);
    }

    @Test
    void getAllPublishedPosts_shouldReturnOnlyPublishedPosts() {
        // given
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published1 = postService.createDraft("Published 1");
        postService.publishPost(published1.getId());

        // when
        List<Post> result = postService.getAllPublishedPosts();

        // then
        assertEquals(1, result.size());
        assertEquals(published1.getId(), result.get(0).getId());
        assertFalse(result.get(0).isDraft());
    }

    @Test
    void getAllPublishedPosts_shouldReturnEmptyListWhenNoPublishedPosts() {
        // given
        postService.createDraft("Only draft");

        // when
        List<Post> result = postService.getAllPublishedPosts();

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllDraftPosts_shouldReturnOnlyDraftPosts() {
        // given
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published = postService.createDraft("Published");
        postService.publishPost(published.getId());

        // when
        List<Post> result = postService.getAllDraftPosts();

        // then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Post::isDraft));
    }

    @Test
    void getAllDraftPosts_shouldReturnEmptyListWhenNoDrafts() {
        // given
        Post published = postService.createDraft("Published");
        postService.publishPost(published.getId());

        // when
        List<Post> result = postService.getAllDraftPosts();

        // then
        assertTrue(result.isEmpty());
    }
}