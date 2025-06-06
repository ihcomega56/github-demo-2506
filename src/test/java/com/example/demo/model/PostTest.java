package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Postモデルクラスの動作とビジネスルールをテストするクラス
 * コンストラクタ、セッター、検索条件マッチング、equals/hashCodeなどの機能を検証する
 */
class PostTest {

    /**
     * デフォルトコンストラクタのテスト
     * コンテンツなしで投稿を作成した場合の初期状態を確認する
     */
    @Test
    void constructor_withoutContent_shouldCreateDraftPost() {
        // when - デフォルトコンストラクタで投稿を作成
        Post post = Post.createDraft();

        // then - 初期状態の検証
        assertNull(post.id()); // IDは未設定
        assertNull(post.content()); // コンテンツは未設定
        assertTrue(post.isDraft()); // デフォルトで下書き状態
        assertNotNull(post.createdAt()); // 作成日時は自動設定
        assertNull(post.updatedAt()); // 更新日時は未設定
        assertNull(post.publishedAt()); // 公開日時は未設定
    }

    /**
     * コンテンツ付きコンストラクタのテスト
     * コンテンツを指定して投稿を作成した場合の初期状態を確認する
     */
    @Test
    void constructor_withContent_shouldCreateDraftPostWithContent() {
        // given - テスト用のコンテンツ
        String content = "Test content";

        // when - コンテンツ付きで投稿を作成
        Post post = Post.createDraft(content);

        // then - 初期状態の検証
        assertNull(post.id()); // IDは未設定
        assertEquals(content, post.content()); // 指定したコンテンツが設定されること
        assertTrue(post.isDraft()); // デフォルトで下書き状態
        assertNotNull(post.createdAt()); // 作成日時は自動設定
        assertNotNull(post.updatedAt()); // 更新日時は自動設定（コンテンツ設定時）
        assertNull(post.publishedAt()); // 公開日時は未設定
    }

    /**
     * コンテンツ更新機能のテスト
     * コンテンツを変更した際に更新日時も自動更新されることを確認する
     */
    @Test
    void setContent_shouldUpdateContentAndUpdatedAt() {
        // given - 初期コンテンツで投稿を作成
        Post post = Post.createDraft("Original content");
        Instant originalUpdatedAt = post.updatedAt();
        
        // 時間差を確保するため少し待機
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when - コンテンツを更新
        Post updatedPost = post.withContent("New content");

        // then - コンテンツと更新日時の変更を確認
        assertEquals("New content", updatedPost.content()); // 新しいコンテンツが設定されること
        assertTrue(updatedPost.updatedAt().isAfter(originalUpdatedAt)); // 更新日時が新しくなること
    }

    /**
     * 下書き状態変更機能のテスト - 公開時の動作
     * 下書きを公開状態に変更した際に公開日時が自動設定されることを確認する
     */
    @Test
    void setDraft_toFalse_shouldSetPublishedAtWhenNull() {
        // given - 下書き投稿を作成（公開日時は未設定）
        Post post = Post.createDraft("Content");
        assertNull(post.publishedAt()); // 初期状態では公開日時未設定

        // when - 下書き状態を解除（公開）
        Post publishedPost = post.withDraft(false);

        // then - 公開状態と公開日時の設定を確認
        assertFalse(publishedPost.isDraft()); // 公開状態になること
        assertNotNull(publishedPost.publishedAt()); // 公開日時が自動設定されること
    }

    /**
     * 下書き状態変更機能のテスト - 既存公開日時の保持
     * 既に公開日時が設定済みの場合、上書きされないことを確認する
     */
    @Test
    void setDraft_toFalse_shouldNotOverrideExistingPublishedAt() {
        // given - カスタム公開日時を事前設定
        Post post = Post.createDraft("Content");
        Instant customPublishedAt = Instant.now().minusSeconds(3600); // 1時間前
        post = post.withPublishedAt(customPublishedAt);

        // when - 下書き状態を解除（公開）
        Post publishedPost = post.withDraft(false);

        // then - 既存の公開日時が保持されることを確認
        assertFalse(publishedPost.isDraft()); // 公開状態になること
        assertEquals(customPublishedAt, publishedPost.publishedAt()); // 既存の公開日時が保持されること
    }

    /**
     * 下書き状態変更機能のテスト - 下書きに戻す場合の動作
     * 公開済み投稿を下書きに戻しても公開日時は保持されることを確認する
     */
    @Test
    void setDraft_toTrue_shouldNotAffectPublishedAt() {
        // given - 投稿を一度公開
        Post post = Post.createDraft("Content");
        post = post.withDraft(false); // 公開
        Instant publishedAt = post.publishedAt();

        // when - 下書き状態に戻す
        Post draftPost = post.withDraft(true);

        // then - 下書き状態になっても公開日時は保持されることを確認
        assertTrue(draftPost.isDraft()); // 下書き状態に戻ること
        assertEquals(publishedAt, draftPost.publishedAt()); // 公開日時は保持されること
    }

    /**
     * 検索条件マッチング機能のテスト - 下書き投稿の場合
     * 下書き投稿は検索対象外であることを確認する
     */
    @Test
    void matchesSearchCriteria_withDraftPost_shouldReturnFalse() {
        // given - 下書き投稿と空の検索条件
        Post draftPost = Post.createDraft("Test content"); // デフォルトで下書き状態
        Post.SearchParams searchParams = new Post.SearchParams(null, null, null);

        // when - 検索条件とのマッチングを確認
        boolean result = draftPost.matchesSearchCriteria(searchParams);

        // then - 下書きは検索対象外なのでfalseが返されること
        assertFalse(result);
    }

    /**
     * 検索条件マッチング機能のテスト - 公開投稿で条件なしの場合
     * 公開投稿は検索条件がnullでもマッチすることを確認する
     */
    @Test
    void matchesSearchCriteria_withPublishedPostAndNullParams_shouldReturnTrue() {
        // given - 公開投稿と検索条件null
        Post post = Post.createDraft("Test content");
        post = post.withDraft(false); // 公開状態に変更

        // when - null条件での検索マッチングを確認
        boolean result = post.matchesSearchCriteria(null);

        // then - 公開投稿は条件なしでもマッチすること
        assertTrue(result);
    }

    /**
     * 検索条件マッチング機能のテスト - コンテンツキーワード検索（大文字小文字無視）
     * コンテンツ内のキーワードが大文字小文字関係なくマッチすることを確認する
     */
    @Test
    void matchesSearchCriteria_withContentKeyword_shouldMatchCaseInsensitive() {
        // given - 公開投稿と小文字のキーワード検索条件
        Post post = Post.createDraft("Hello World Test");
        post = post.withDraft(false); // 公開状態
        Post.SearchParams searchParams = new Post.SearchParams("hello", null, null);

        // when - キーワード検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - 大文字小文字関係なくマッチすること
        assertTrue(result);
    }

    /**
     * 検索条件マッチング機能のテスト - コンテンツキーワード検索（マッチしない場合）
     * 存在しないキーワードではマッチしないことを確認する
     */
    @Test
    void matchesSearchCriteria_withContentKeyword_shouldNotMatchWhenNotContained() {
        // given - 公開投稿と存在しないキーワード
        Post post = Post.createDraft("Hello World");
        post = post.withDraft(false); // 公開状態
        Post.SearchParams searchParams = new Post.SearchParams("xyz", null, null);

        // when - キーワード検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - マッチしないことを確認
        assertFalse(result);
    }

    /**
     * 検索条件マッチング機能のテスト - 公開日時以降の条件（マッチする場合）
     * 指定日時以降に公開された投稿がマッチすることを確認する
     */
    @Test
    void matchesSearchCriteria_withPublishedAfter_shouldMatchWhenPublishedAfterDate() {
        // given - 公開投稿と検索基準日時（投稿より1時間前）
        Post post = Post.createDraft("Content");
        post = post.withDraft(false); // 公開
        Instant searchDate = post.publishedAt().minusSeconds(3600); // 公開時刻より1時間前
        Post.SearchParams searchParams = new Post.SearchParams(null, searchDate, null);

        // when - 公開日時以降の検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - 検索日時以降に公開されているのでマッチすること
        assertTrue(result);
    }

    /**
     * 検索条件マッチング機能のテスト - 公開日時以降の条件（マッチしない場合）
     * 指定日時より前に公開された投稿がマッチしないことを確認する
     */
    @Test
    void matchesSearchCriteria_withPublishedAfter_shouldNotMatchWhenPublishedBeforeDate() {
        // given - 公開投稿と検索基準日時（投稿より1時間後）
        Post post = Post.createDraft("Content");
        post = post.withDraft(false); // 公開
        Instant searchDate = post.publishedAt().plusSeconds(3600); // 公開時刻より1時間後
        Post.SearchParams searchParams = new Post.SearchParams(null, searchDate, null);

        // when - 公開日時以降の検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - 検索日時より前に公開されているのでマッチしないこと
        assertFalse(result);
    }

    /**
     * 検索条件マッチング機能のテスト - 公開日時以前の条件（マッチする場合）
     * 指定日時より前に公開された投稿がマッチすることを確認する
     */
    @Test
    void matchesSearchCriteria_withPublishedBefore_shouldMatchWhenPublishedBeforeDate() {
        // given - 公開投稿と検索基準日時（投稿より1時間後）
        Post post = Post.createDraft("Content");
        post = post.withDraft(false); // 公開
        Instant searchDate = post.publishedAt().plusSeconds(3600); // 公開時刻より1時間後
        Post.SearchParams searchParams = new Post.SearchParams(null, null, searchDate);

        // when - 公開日時以前の検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - 検索日時より前に公開されているのでマッチすること
        assertTrue(result);
    }

    /**
     * 検索条件マッチング機能のテスト - 公開日時以前の条件（マッチしない場合）
     * 指定日時以降に公開された投稿がマッチしないことを確認する
     */
    @Test
    void matchesSearchCriteria_withPublishedBefore_shouldNotMatchWhenPublishedAfterDate() {
        // given - 公開投稿と検索基準日時（投稿より1時間前）
        Post post = Post.createDraft("Content");
        post = post.withDraft(false); // 公開
        Instant searchDate = post.publishedAt().minusSeconds(3600); // 公開時刻より1時間前
        Post.SearchParams searchParams = new Post.SearchParams(null, null, searchDate);

        // when - 公開日時以前の検索を実行
        boolean result = post.matchesSearchCriteria(searchParams);

        // then - 検索日時より後に公開されているのでマッチしないこと
        assertFalse(result);
    }

    /**
     * equals機能のテスト - 同じIDの場合
     * 同じIDを持つ投稿同士がequalsでtrueを返すことを確認する
     */
    @Test
    void equals_shouldReturnTrueForSameId() {
        // given - 同じIDを持つ2つの投稿（コンテンツは異なる）
        Post post1 = Post.createDraft("Content 1");
        post1 = post1.withId(1L);
        Post post2 = Post.createDraft("Content 2");
        post2 = post2.withId(1L); // 同じID

        // when & then - 同じIDなのでequalsでtrueが返されることを確認
        assertEquals(post1, post2);
    }

    /**
     * equals機能のテスト - 異なるIDの場合
     * 異なるIDを持つ投稿同士がequalsでfalseを返すことを確認する
     */
    @Test
    void equals_shouldReturnFalseForDifferentId() {
        // given - 異なるIDを持つ2つの投稿（コンテンツは同じ）
        Post post1 = Post.createDraft("Content");
        post1 = post1.withId(1L);
        Post post2 = Post.createDraft("Content");
        post2 = post2.withId(2L); // 異なるID

        // when & then - 異なるIDなのでequalsでfalseが返されることを確認
        assertNotEquals(post1, post2);
    }

    /**
     * hashCode機能のテスト
     * equalsで等しい投稿同士のhashCodeが一致することを確認する
     */
    @Test
    void hashCode_shouldBeConsistentWithEquals() {
        // given - 同じIDを持つ2つの投稿（equalsでtrue）
        Post post1 = Post.createDraft("Content 1");
        post1 = post1.withId(1L);
        Post post2 = Post.createDraft("Content 2");
        post2 = post2.withId(1L); // 同じID

        // when & then - equalsで等しいオブジェクトのhashCodeが一致することを確認
        assertEquals(post1.hashCode(), post2.hashCode());
    }

    /**
     * toString機能のテスト
     * toStringメソッドが主要なフィールドを含む文字列を返すことを確認する
     */
    @Test
    void toString_shouldContainKeyFields() {
        // given - テスト用の投稿を作成
        Post post = Post.createDraft("Test content");
        post = post.withId(1L);

        // when - toString()を呼び出し
        String result = post.toString();

        // then - 主要フィールドが文字列に含まれることを確認
        assertTrue(result.contains("id=1")); // IDが含まれること
        assertTrue(result.contains("content='Test content'")); // コンテンツが含まれること
        assertTrue(result.contains("isDraft=true")); // 下書き状態が含まれること
    }
}