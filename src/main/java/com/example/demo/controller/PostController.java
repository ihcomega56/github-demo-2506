package com.example.demo.controller;

import com.example.demo.config.DeploymentInfo;
import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService postService;
    private final DeploymentInfo deploymentInfo;
    
    public PostController(PostService postService, DeploymentInfo deploymentInfo) {
        this.postService = postService;
        this.deploymentInfo = deploymentInfo;
    }
    
    @PostMapping("/drafts")
    public ResponseEntity<Post> createDraft(@RequestBody Map<String, String> payload) {
        var content = payload.get("content");
        
        if (content == null) {
            return ResponseEntity.badRequest().build();
        }
        
        var createdPost = postService.createDraft(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    @PutMapping("/drafts/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.publishPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.getPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<Post>> getAllPublishedPosts() {
        var posts = postService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/drafts")
    public ResponseEntity<List<Post>> getAllDraftPosts() {
        var posts = postService.getAllDraftPosts();
        return ResponseEntity.ok(posts);
    }
    
    @RestController
    public class TestController {
        @GetMapping("/test")
        public String testEndpoint() {
            return "<html><body><h1 style='font-size:48px;'>Hello World! 🌍</h1><p style='font-size:24px;'>Deployed at: " + deploymentInfo.getDeployedAt() + "</p></body></html>";
        }
    }
}
