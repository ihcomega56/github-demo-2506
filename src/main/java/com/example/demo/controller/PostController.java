package com.example.demo.controller;

import com.example.demo.config.DeploymentInfo;
import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 投稿に関するAPIエンドポイントを提供するコントローラークラス。
 * 投稿の作成、更新、削除、取得など基本的なCRUD操作を担当します。
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService postService;
    private final DeploymentInfo deploymentInfo;
    
    /**
     * コントローラーのコンストラクタ。
     * 
     * @param postService 投稿サービスのインスタンス
     */
    public PostController(PostService postService, DeploymentInfo deploymentInfo) {
        this.postService = postService;
        this.deploymentInfo = deploymentInfo;
    }
    
    /**
     * 下書き投稿を作成するエンドポイント。
     * 
     * @param payload 投稿内容を含むリクエストボディ（"content"キーが必須）
     * @return 作成された投稿情報とHTTPステータス201（Created）、またはエラー時は400（Bad Request）
     */
    @PostMapping("/drafts")
    public ResponseEntity<Post> createDraft(@RequestBody Map<String, String> payload) {
        var content = payload.get("content");
        
        if (content == null) {
            return ResponseEntity.badRequest().build();
        }
        
        var createdPost = postService.createDraft(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    /**
     * 下書き投稿を公開するエンドポイント。
     * 
     * @param id 公開する投稿のID
     * @return 公開された投稿情報とHTTPステータス200（OK）、または投稿が見つからない場合は404（Not Found）
     */
    @PutMapping("/drafts/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.publishPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 指定されたIDの投稿を取得するエンドポイント。
     * 
     * @param id 取得する投稿のID
     * @return 投稿情報とHTTPステータス200（OK）、または投稿が見つからない場合は404（Not Found）
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return Optional.ofNullable(postService.getPost(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 指定されたIDの投稿を削除するエンドポイント。
     * 
     * @param id 削除する投稿のID
     * @return 削除成功時はHTTPステータス204（No Content）、投稿が見つからない場合は404（Not Found）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * 公開済みの全投稿を取得するエンドポイント。
     * 
     * @return 公開済み投稿のリストとHTTPステータス200（OK）
     */
    @GetMapping("/published")
    public ResponseEntity<List<Post>> getAllPublishedPosts() {
        var posts = postService.getPublishedPosts();
        return ResponseEntity.ok(posts);
    }
    
    /**
     * 下書き状態の全投稿を取得するエンドポイント。
     * 
     * @return 下書き投稿のリストとHTTPステータス200（OK）
     */
    @GetMapping("/drafts")
    public ResponseEntity<List<Post>> getAllDraftPosts() {
        var posts = postService.getDraftPosts();
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
