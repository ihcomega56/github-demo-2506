package com.example.demo.model;

import java.time.Instant;

public class Post {
    private Long id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private boolean isDraft;

    public Post() {
        this.createdAt = Instant.now();
        this.isDraft = true;
    }

    public Post(String content) {
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isDraft = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
        if (!draft && publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

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
    
    public record SearchParams(
            String contentKeyword,
            Instant publishedAfter,
            Instant publishedBefore
    ) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;
        return id != null ? id.equals(post.id) : post.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isDraft=" + isDraft +
                '}';
    }
}
