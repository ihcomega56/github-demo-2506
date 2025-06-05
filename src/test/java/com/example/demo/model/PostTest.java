package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void constructor_withoutContent_shouldCreateDraftPost() {
        // when
        Post post = new Post();

        // then
        assertNull(post.getId());
        assertNull(post.getContent());
        assertTrue(post.isDraft());
        assertNotNull(post.getCreatedAt());
        assertNull(post.getUpdatedAt());
        assertNull(post.getPublishedAt());
    }

    @Test
    void constructor_withContent_shouldCreateDraftPostWithContent() {
        // given
        String content = "Test content";

        // when
        Post post = new Post(content);

        // then
        assertNull(post.getId());
        assertEquals(content, post.getContent());
        assertTrue(post.isDraft());
        assertNotNull(post.getCreatedAt());
        assertNotNull(post.getUpdatedAt());
        assertNull(post.getPublishedAt());
    }

    @Test
    void setContent_shouldUpdateContentAndUpdatedAt() {
        // given
        Post post = new Post("Original content");
        Instant originalUpdatedAt = post.getUpdatedAt();
        
        // wait a bit to ensure time difference
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when
        post.setContent("New content");

        // then
        assertEquals("New content", post.getContent());
        assertTrue(post.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void setDraft_toFalse_shouldSetPublishedAtWhenNull() {
        // given
        Post post = new Post("Content");
        assertNull(post.getPublishedAt());

        // when
        post.setDraft(false);

        // then
        assertFalse(post.isDraft());
        assertNotNull(post.getPublishedAt());
    }

    @Test
    void setDraft_toFalse_shouldNotOverrideExistingPublishedAt() {
        // given
        Post post = new Post("Content");
        Instant customPublishedAt = Instant.now().minusSeconds(3600); // 1 hour ago
        post.setPublishedAt(customPublishedAt);

        // when
        post.setDraft(false);

        // then
        assertFalse(post.isDraft());
        assertEquals(customPublishedAt, post.getPublishedAt());
    }

    @Test
    void setDraft_toTrue_shouldNotAffectPublishedAt() {
        // given
        Post post = new Post("Content");
        post.setDraft(false); // publish first
        Instant publishedAt = post.getPublishedAt();

        // when
        post.setDraft(true);

        // then
        assertTrue(post.isDraft());
        assertEquals(publishedAt, post.getPublishedAt());
    }

    @Test
    void matchesSearchCriteria_withDraftPost_shouldReturnFalse() {
        // given
        Post draftPost = new Post("Test content");
        Post.SearchParams searchParams = new Post.SearchParams();

        // when
        boolean result = draftPost.matchesSearchCriteria(searchParams);

        // then
        assertFalse(result);
    }

    @Test
    void matchesSearchCriteria_withPublishedPostAndNullParams_shouldReturnTrue() {
        // given
        Post post = new Post("Test content");
        post.setDraft(false);

        // when
        boolean result = post.matchesSearchCriteria(null);

        // then
        assertTrue(result);
    }

    @Test
    void matchesSearchCriteria_withContentKeyword_shouldMatchCaseInsensitive() {
        // given
        Post post = new Post("Hello World Test");
        post.setDraft(false);
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setContentKeyword("hello");

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertTrue(result);
    }

    @Test
    void matchesSearchCriteria_withContentKeyword_shouldNotMatchWhenNotContained() {
        // given
        Post post = new Post("Hello World");
        post.setDraft(false);
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setContentKeyword("xyz");

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertFalse(result);
    }

    @Test
    void matchesSearchCriteria_withPublishedAfter_shouldMatchWhenPublishedAfterDate() {
        // given
        Post post = new Post("Content");
        post.setDraft(false);
        Instant searchDate = post.getPublishedAt().minusSeconds(3600); // 1 hour before
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setPublishedAfter(searchDate);

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertTrue(result);
    }

    @Test
    void matchesSearchCriteria_withPublishedAfter_shouldNotMatchWhenPublishedBeforeDate() {
        // given
        Post post = new Post("Content");
        post.setDraft(false);
        Instant searchDate = post.getPublishedAt().plusSeconds(3600); // 1 hour after
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setPublishedAfter(searchDate);

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertFalse(result);
    }

    @Test
    void matchesSearchCriteria_withPublishedBefore_shouldMatchWhenPublishedBeforeDate() {
        // given
        Post post = new Post("Content");
        post.setDraft(false);
        Instant searchDate = post.getPublishedAt().plusSeconds(3600); // 1 hour after
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setPublishedBefore(searchDate);

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertTrue(result);
    }

    @Test
    void matchesSearchCriteria_withPublishedBefore_shouldNotMatchWhenPublishedAfterDate() {
        // given
        Post post = new Post("Content");
        post.setDraft(false);
        Instant searchDate = post.getPublishedAt().minusSeconds(3600); // 1 hour before
        Post.SearchParams searchParams = new Post.SearchParams();
        searchParams.setPublishedBefore(searchDate);

        // when
        boolean result = post.matchesSearchCriteria(searchParams);

        // then
        assertFalse(result);
    }

    @Test
    void equals_shouldReturnTrueForSameId() {
        // given
        Post post1 = new Post("Content 1");
        post1.setId(1L);
        Post post2 = new Post("Content 2");
        post2.setId(1L);

        // when & then
        assertEquals(post1, post2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        // given
        Post post1 = new Post("Content");
        post1.setId(1L);
        Post post2 = new Post("Content");
        post2.setId(2L);

        // when & then
        assertNotEquals(post1, post2);
    }

    @Test
    void hashCode_shouldBeConsistentWithEquals() {
        // given
        Post post1 = new Post("Content 1");
        post1.setId(1L);
        Post post2 = new Post("Content 2");
        post2.setId(1L);

        // when & then
        assertEquals(post1.hashCode(), post2.hashCode());
    }

    @Test
    void toString_shouldContainKeyFields() {
        // given
        Post post = new Post("Test content");
        post.setId(1L);

        // when
        String result = post.toString();

        // then
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("content='Test content'"));
        assertTrue(result.contains("isDraft=true"));
    }
}