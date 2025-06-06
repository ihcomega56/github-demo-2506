package com.example.demo.model;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Post(
        Long id,
        String content,
        Instant createdAt,
        Instant updatedAt,
        Instant publishedAt,
        @JsonProperty("draft") boolean isDraft
) {
    
    // Factory method for creating a new draft post without content
    public static Post createDraft() {
        return new Post(
                null,
                null,
                Instant.now(),
                null,
                null,
                true
        );
    }
    
    // Factory method for creating a new draft post with content
    public static Post createDraft(String content) {
        Instant now = Instant.now();
        return new Post(
                null,
                content,
                now,
                now,
                null,
                true
        );
    }
    
    // Utility method to create a new Post with an updated id
    public Post withId(Long id) {
        return new Post(id, content, createdAt, updatedAt, publishedAt, isDraft);
    }
    
    // Utility method to create a new Post with updated content
    public Post withContent(String content) {
        return new Post(id, content, createdAt, Instant.now(), publishedAt, isDraft);
    }
    
    // Utility method to create a new Post with updated createdAt
    public Post withCreatedAt(Instant createdAt) {
        return new Post(id, content, createdAt, updatedAt, publishedAt, isDraft);
    }
    
    // Utility method to create a new Post with updated updatedAt
    public Post withUpdatedAt(Instant updatedAt) {
        return new Post(id, content, createdAt, updatedAt, publishedAt, isDraft);
    }
    
    // Utility method to create a new Post with updated publishedAt
    public Post withPublishedAt(Instant publishedAt) {
        return new Post(id, content, createdAt, updatedAt, publishedAt, isDraft);
    }
    
    // Utility method to create a new Post with updated draft status
    public Post withDraft(boolean draft) {
        Instant newPublishedAt = publishedAt;
        if (!draft && publishedAt == null) {
            newPublishedAt = Instant.now();
        }
        return new Post(id, content, createdAt, updatedAt, newPublishedAt, draft);
    }

    
    // Business logic method for search criteria matching
    public boolean matchesSearchCriteria(SearchParams searchParams) {
        if (this.isDraft || this.publishedAt == null) {
            return false;
        }
        
        if (searchParams == null) {
            return true;
        }
        
        if (searchParams.contentKeyword() != null && !searchParams.contentKeyword().isEmpty()) {
            if (this.content == null) {
                return false;
            }
            
            String contentLower = this.content.toLowerCase();
            String keywordLower = searchParams.contentKeyword().toLowerCase();
            
            if (!contentLower.contains(keywordLower)) {
                return false;
            }
        }
        
        if (searchParams.publishedAfter() != null) {
            if (this.publishedAt.isBefore(searchParams.publishedAfter())) {
                return false;
            }
        }
        
        if (searchParams.publishedBefore() != null) {
            if (this.publishedAt.isAfter(searchParams.publishedBefore())) {
                return false;
            }
        }
        
        return true;
    }
    
    // Override equals to only use id (like the original implementation)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;
        return id != null ? id.equals(post.id) : post.id == null;
    }

    // Override hashCode to only use id (like the original implementation)
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // Override toString to match original format
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isDraft=" + isDraft +
                '}';
    }
    
    public record SearchParams(
            String contentKeyword,
            Instant publishedAfter,
            Instant publishedBefore
    ) {}
}
