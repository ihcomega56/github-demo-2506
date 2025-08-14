package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.DeploymentInfo;
import com.example.demo.model.Post;
import com.example.demo.service.PostService;

/**
 * PostControllerのREST APIエンドポイントをテストするクラス
 * MockMvcを使用してHTTPリクエスト/レスポンスのテストを実行する
 * PostServiceはMockとしてモック化している
 */
@WebMvcTest(PostController.class)
@Import(TestConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレートするためのMockMvc

    @Autowired
    private PostService postService; // PostServiceのモック
    
    @Autowired
    private DeploymentInfo deploymentInfo; // DeploymentInfoのモック

    /**
     * 下書き投稿作成APIのテスト - 正常系
     * 有効なコンテンツで下書きを作成し、HTTP 201が返されることを確認する
     */
    @Test
    void createDraft_shouldReturnCreatedPost() throws Exception {
        // given - テストデータの準備
        String content = "Test content";
        Post createdPost = new Post(content);
        createdPost.setId(1L);

        when(postService.createDraft(content)).thenReturn(createdPost);

        // when & then - APIを呼び出してレスポンスを検証
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"" + content + "\"}"))
                .andExpect(status().isCreated()) // HTTP 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.draft").value(true)); // 下書き状態であることを確認
    }

    /**
     * 下書き投稿作成APIのテスト - 異常系
     * コンテンツがnullの場合、HTTP 400が返されることを確認する
     */
    @Test
    void createDraft_shouldReturnBadRequestWhenContentIsNull() throws Exception {
        // when & then - 空のJSONリクエストを送信してHTTP 400を期待
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // HTTP 400 Bad Request
    }

    /**
     * 下書き投稿作成APIのテスト - 異常系
     * コンテンツが空文字列の場合、HTTP 400が返されることを確認する
     */
    @Test
    void createDraft_shouldReturnBadRequestWhenContentIsEmpty() throws Exception {
        // when & then - 空文字列のJSONリクエストを送信してHTTP 400を期待
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest()); // HTTP 400 Bad Request
    }

    /**
     * 下書き投稿作成APIのテスト - 異常系
     * コンテンツが空白文字のみの場合、HTTP 400が返されることを確認する
     */
    @Test
    void createDraft_shouldReturnBadRequestWhenContentIsBlank() throws Exception {
        // when & then - 空白文字のみのJSONリクエストを送信してHTTP 400を期待
        mockMvc.perform(post("/api/posts/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest()); // HTTP 400 Bad Request
    }

    /**
     * 投稿公開APIのテスト - 正常系
     * 既存の下書きを公開し、HTTP 200と更新された投稿が返されることを確認する
     */
    @Test
    void publishPost_shouldReturnPublishedPost() throws Exception {
        // given - 公開対象の投稿データを準備
        Long postId = 1L;
        Post publishedPost = new Post("Content");
        publishedPost.setId(postId);
        publishedPost.setDraft(false); // 公開状態に設定

        when(postService.publishPost(postId)).thenReturn(publishedPost);

        // when & then - 公開APIを呼び出してレスポンスを検証
        mockMvc.perform(put("/api/posts/drafts/{id}/publish", postId))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.draft").value(false)); // 公開状態であることを確認
    }

    /**
     * 投稿公開APIのテスト - 異常系
     * 存在しない投稿を公開しようとした場合、HTTP 404が返されることを確認する
     */
    @Test
    void publishPost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given - 存在しない投稿IDを設定
        Long postId = 999L;
        when(postService.publishPost(postId)).thenReturn(null); // サービスがnullを返すようにモック

        // when & then - APIを呼び出してHTTP 404を期待
        mockMvc.perform(put("/api/posts/drafts/{id}/publish", postId))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }

    /**
     * 投稿取得APIのテスト - 正常系
     * 既存の投稿を取得し、HTTP 200と投稿データが返されることを確認する
     */
    @Test
    void getPost_shouldReturnExistingPost() throws Exception {
        // given - 取得対象の投稿データを準備
        Long postId = 1L;
        Post post = new Post("Content");
        post.setId(postId);

        when(postService.getPost(postId)).thenReturn(post);

        // when & then - 取得APIを呼び出してレスポンスを検証
        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("Content"));
    }

    /**
     * 投稿取得APIのテスト - 異常系
     * 存在しない投稿を取得しようとした場合、HTTP 404が返されることを確認する
     */
    @Test
    void getPost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given - 存在しない投稿IDを設定
        Long postId = 999L;
        when(postService.getPost(postId)).thenReturn(null); // サービスがnullを返すようにモック

        // when & then - APIを呼び出してHTTP 404を期待
        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }

    /**
     * 投稿削除APIのテスト - 正常系
     * 既存の投稿を削除し、HTTP 204が返されることを確認する
     */
    @Test
    void deletePost_shouldReturnNoContentWhenPostExists() throws Exception {
        // given - 削除対象の投稿IDを設定
        Long postId = 1L;
        when(postService.deletePost(postId)).thenReturn(true); // 削除成功をモック

        // when & then - 削除APIを呼び出してHTTP 204を期待
        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }

    /**
     * 投稿削除APIのテスト - 異常系
     * 存在しない投稿を削除しようとした場合、HTTP 404が返されることを確認する
     */
    @Test
    void deletePost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given - 存在しない投稿IDを設定
        Long postId = 999L;
        when(postService.deletePost(postId)).thenReturn(false); // 削除失敗をモック

        // when & then - APIを呼び出してHTTP 404を期待
        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }

    /**
     * 公開投稿一覧取得APIのテスト
     * 公開された投稿のリストが正しく返されることを確認する
     */
    @Test
    void getPublishedPosts_shouldReturnListOfPublishedPosts() throws Exception {
        // given - 公開投稿リストを準備
        Post post1 = new Post("Content 1");
        post1.setId(1L);
        post1.setDraft(false); // 公開状態
        Post post2 = new Post("Content 2");
        post2.setId(2L);
        post2.setDraft(false); // 公開状態
        List<Post> publishedPosts = Arrays.asList(post1, post2);

        when(postService.getPublishedPosts()).thenReturn(publishedPosts);

        // when & then - 公開投稿一覧APIを呼び出してレスポンスを検証
        mockMvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // 2件の投稿
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    /**
     * 下書き投稿一覧取得APIのテスト
     * 下書き状態の投稿のリストが正しく返されることを確認する
     */
    @Test
    void getDraftPosts_shouldReturnListOfDraftPosts() throws Exception {
        // given - 下書き投稿リストを準備
        Post draft1 = new Post("Draft 1");
        draft1.setId(1L); // デフォルトで下書き状態
        Post draft2 = new Post("Draft 2");
        draft2.setId(2L); // デフォルトで下書き状態
        List<Post> draftPosts = Arrays.asList(draft1, draft2);

        when(postService.getDraftPosts()).thenReturn(draftPosts);

        // when & then - 下書き投稿一覧APIを呼び出してレスポンスを検証
        mockMvc.perform(get("/api/posts/drafts"))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // 2件の下書き
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].draft").value(true)) // 下書き状態を確認
                .andExpect(jsonPath("$[1].draft").value(true)); // 下書き状態を確認
    }
    
    /**
     * 投稿いいね追加APIのテスト - 正常系
     * 既存の投稿にいいねを追加し、HTTP 200といいね追加後の投稿が返されることを確認する
     */
    @Test
    void likePost_shouldReturnUpdatedPost() throws Exception {
        // given - いいね対象の投稿データを準備
        Long postId = 1L;
        Post likedPost = new Post("Content");
        likedPost.setId(postId);
        likedPost.setLikes(1); // いいね追加後の状態

        when(postService.likePost(postId)).thenReturn(likedPost);

        // when & then - いいね追加APIを呼び出してレスポンスを検証
        mockMvc.perform(post("/api/posts/{id}/like", postId))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.likes").value(1)); // いいね数が1であることを確認
    }
    
    /**
     * 投稿いいね追加APIのテスト - 異常系
     * 存在しない投稿にいいねを追加しようとした場合、HTTP 404が返されることを確認する
     */
    @Test
    void likePost_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given - 存在しない投稿IDを設定
        Long postId = 999L;
        when(postService.likePost(postId)).thenReturn(null); // サービスがnullを返すようにモック

        // when & then - APIを呼び出してHTTP 404を期待
        mockMvc.perform(post("/api/posts/{id}/like", postId))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }
    
    /**
     * 投稿いいね数取得APIのテスト - 正常系
     * 既存の投稿のいいね数を取得し、HTTP 200といいね数が返されることを確認する
     */
    @Test
    void getPostLikes_shouldReturnLikesCount() throws Exception {
        // given - いいね数取得対象の投稿IDを設定
        Long postId = 1L;
        Integer likesCount = 5; // いいね数

        when(postService.getPostLikes(postId)).thenReturn(likesCount);

        // when & then - いいね数取得APIを呼び出してレスポンスを検証
        mockMvc.perform(get("/api/posts/{id}/likes", postId))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.likes").value(5)); // いいね数が正しく返されること
    }
    
    /**
     * 投稿いいね数取得APIのテスト - 異常系
     * 存在しない投稿のいいね数を取得しようとした場合、HTTP 404が返されることを確認する
     */
    @Test
    void getPostLikes_shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        // given - 存在しない投稿IDを設定
        Long postId = 999L;
        when(postService.getPostLikes(postId)).thenReturn(null); // サービスがnullを返すようにモック

        // when & then - APIを呼び出してHTTP 404を期待
        mockMvc.perform(get("/api/posts/{id}/likes", postId))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }
}