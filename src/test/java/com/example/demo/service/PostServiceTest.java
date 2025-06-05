package com.example.demo.service;

import com.example.demo.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostServiceのビジネスロジックをテストするクラス
 * 投稿の作成、公開、削除、取得などの機能を総合的にテストする
 */
class PostServiceTest {

    private PostService postService; // テスト対象のPostServiceインスタンス

    /**
     * 各テストメソッド実行前にPostServiceの新しいインスタンスを作成
     */
    @BeforeEach
    void setUp() {
        postService = new PostService();
    }

    /**
     * 下書き作成機能のテスト - 正常系
     * 指定されたコンテンツで下書きが正しく作成されることを確認する
     */
    @Test
    void createDraft_shouldCreatePostWithCorrectContent() {
        // given - テストデータの準備
        String content = "Test content";

        // when - 下書きを作成
        Post result = postService.createDraft(content);

        // then - 作成された投稿の検証
        assertNotNull(result); // 投稿が作成されていること
        assertEquals(content, result.getContent()); // コンテンツが正しく設定されていること
        assertTrue(result.isDraft()); // 下書き状態であること
        assertNotNull(result.getId()); // IDが生成されていること
        assertNotNull(result.getCreatedAt()); // 作成日時が設定されていること
        assertNotNull(result.getUpdatedAt()); // 更新日時が設定されていること
        assertNull(result.getPublishedAt()); // 公開日時は未設定であること
    }

    /**
     * ID生成機能のテスト
     * 複数の投稿を作成した際に、それぞれ異なるIDが生成されることを確認する
     */
    @Test
    void createDraft_shouldGenerateUniqueIds() {
        // given - 異なるコンテンツで2つの投稿を準備
        String content1 = "First post";
        String content2 = "Second post";

        // when - 2つの下書きを作成
        Post post1 = postService.createDraft(content1);
        Post post2 = postService.createDraft(content2);

        // then - IDが重複していないことを確認
        assertNotEquals(post1.getId(), post2.getId());
    }

    /**
     * 投稿公開機能のテスト - 正常系
     * 既存の下書きを公開状態に変更できることを確認する
     */
    @Test
    void publishPost_shouldPublishExistingDraft() {
        // given - 公開対象の下書きを事前に作成
        Post draft = postService.createDraft("Draft content");
        Long draftId = draft.getId();

        // when - 下書きを公開
        Post result = postService.publishPost(draftId);

        // then - 公開処理の結果を検証
        assertNotNull(result); // 公開された投稿が返されること
        assertEquals(draftId, result.getId()); // IDが変更されていないこと
        assertFalse(result.isDraft()); // 下書き状態が解除されていること
        assertNotNull(result.getPublishedAt()); // 公開日時が設定されていること
    }

    /**
     * 投稿公開機能のテスト - 異常系（存在しない投稿）
     * 存在しない投稿IDを指定した場合、nullが返されることを確認する
     */
    @Test
    void publishPost_shouldReturnNullForNonexistentPost() {
        // given - 存在しない投稿ID
        Long nonexistentId = 999L;

        // when - 存在しない投稿を公開しようと試行
        Post result = postService.publishPost(nonexistentId);

        // then - nullが返されることを確認
        assertNull(result);
    }

    /**
     * 投稿公開機能のテスト - 異常系（既に公開済み）
     * 既に公開済みの投稿を再度公開しようとした場合、nullが返されることを確認する
     */
    @Test
    void publishPost_shouldReturnNullForAlreadyPublishedPost() {
        // given - 下書きを作成して一度公開
        Post draft = postService.createDraft("Draft content");
        Long draftId = draft.getId();
        postService.publishPost(draftId); // 1回目の公開

        // when - 同じ投稿を再度公開しようと試行
        Post result = postService.publishPost(draftId);

        // then - nullが返されることを確認（重複公開は不可）
        assertNull(result);
    }

    /**
     * 投稿削除機能のテスト - 正常系
     * 既存の投稿を削除し、削除後は取得できなくなることを確認する
     */
    @Test
    void deletePost_shouldReturnTrueForExistingPost() {
        // given - 削除対象の投稿を事前に作成
        Post post = postService.createDraft("Content to delete");
        Long postId = post.getId();

        // when - 投稿を削除
        boolean result = postService.deletePost(postId);

        // then - 削除が成功し、投稿が取得できなくなることを確認
        assertTrue(result); // 削除が成功したこと
        assertNull(postService.getPost(postId)); // 削除後は取得できないこと
    }

    /**
     * 投稿削除機能のテスト - 異常系
     * 存在しない投稿を削除しようとした場合、falseが返されることを確認する
     */
    @Test
    void deletePost_shouldReturnFalseForNonexistentPost() {
        // given - 存在しない投稿ID
        Long nonexistentId = 999L;

        // when - 存在しない投稿を削除しようと試行
        boolean result = postService.deletePost(nonexistentId);

        // then - falseが返されることを確認
        assertFalse(result);
    }

    /**
     * 投稿取得機能のテスト - 正常系
     * 既存の投稿を正しく取得できることを確認する
     */
    @Test
    void getPost_shouldReturnExistingPost() {
        // given - 取得対象の投稿を事前に作成
        Post originalPost = postService.createDraft("Test content");
        Long postId = originalPost.getId();

        // when - 投稿を取得
        Post result = postService.getPost(postId);

        // then - 正しい投稿が取得されることを確認
        assertNotNull(result); // 投稿が取得できること
        assertEquals(postId, result.getId()); // IDが一致すること
        assertEquals("Test content", result.getContent()); // コンテンツが一致すること
    }

    /**
     * 投稿取得機能のテスト - 異常系
     * 存在しない投稿を取得しようとした場合、nullが返されることを確認する
     */
    @Test
    void getPost_shouldReturnNullForNonexistentPost() {
        // given - 存在しない投稿ID
        Long nonexistentId = 999L;

        // when - 存在しない投稿を取得しようと試行
        Post result = postService.getPost(nonexistentId);

        // then - nullが返されることを確認
        assertNull(result);
    }

    /**
     * 公開投稿一覧取得機能のテスト
     * 公開状態の投稿のみがリストに含まれることを確認する
     */
    @Test
    void getAllPublishedPosts_shouldReturnOnlyPublishedPosts() {
        // given - 下書きと公開投稿を混在させて作成
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published1 = postService.createDraft("Published 1");
        postService.publishPost(published1.getId()); // 1つだけ公開

        // when - 公開投稿一覧を取得
        List<Post> result = postService.getAllPublishedPosts();

        // then - 公開投稿のみが取得されることを確認
        assertEquals(1, result.size()); // 公開投稿は1件のみ
        assertEquals(published1.getId(), result.get(0).getId()); // 正しい投稿が取得されること
        assertFalse(result.get(0).isDraft()); // 公開状態であることを確認
    }

    /**
     * 公開投稿一覧取得機能のテスト - 公開投稿が存在しない場合
     * 公開投稿が1件もない場合、空のリストが返されることを確認する
     */
    @Test
    void getAllPublishedPosts_shouldReturnEmptyListWhenNoPublishedPosts() {
        // given - 下書きのみを作成（公開投稿なし）
        postService.createDraft("Only draft");

        // when - 公開投稿一覧を取得
        List<Post> result = postService.getAllPublishedPosts();

        // then - 空のリストが返されることを確認
        assertTrue(result.isEmpty());
    }

    /**
     * 下書き投稿一覧取得機能のテスト
     * 下書き状態の投稿のみがリストに含まれることを確認する
     */
    @Test
    void getAllDraftPosts_shouldReturnOnlyDraftPosts() {
        // given - 下書きと公開投稿を混在させて作成
        Post draft1 = postService.createDraft("Draft 1");
        Post draft2 = postService.createDraft("Draft 2");
        Post published = postService.createDraft("Published");
        postService.publishPost(published.getId()); // 1つを公開

        // when - 下書き投稿一覧を取得
        List<Post> result = postService.getAllDraftPosts();

        // then - 下書き投稿のみが取得されることを確認
        assertEquals(2, result.size()); // 下書きは2件
        assertTrue(result.stream().allMatch(Post::isDraft)); // 全て下書き状態であること
    }

    /**
     * 下書き投稿一覧取得機能のテスト - 下書きが存在しない場合
     * 下書きが1件もない場合、空のリストが返されることを確認する
     */
    @Test
    void getAllDraftPosts_shouldReturnEmptyListWhenNoDrafts() {
        // given - 公開投稿のみを作成（下書きなし）
        Post published = postService.createDraft("Published");
        postService.publishPost(published.getId());

        // when - 下書き投稿一覧を取得
        List<Post> result = postService.getAllDraftPosts();

        // then - 空のリストが返されることを確認
        assertTrue(result.isEmpty());
    }
}