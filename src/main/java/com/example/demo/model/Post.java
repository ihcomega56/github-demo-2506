package com.example.demo.model;

import java.time.Instant;

/**
 * 投稿エンティティを表すモデルクラス。
 * 投稿の内容、作成日時、更新日時、公開日時、下書き状態などの情報を保持します。
 */
public class Post {
    private Long id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private boolean isDraft;

    /**
     * デフォルトコンストラクタ。
     * 新しい下書き投稿を現在時刻の作成日時で初期化します。
     */
    public Post() {
        this.createdAt = Instant.now();
        this.isDraft = true;
    }

    /**
     * 投稿内容を指定するコンストラクタ。
     * 新しい下書き投稿を現在時刻の作成日時と更新日時で初期化します。
     *
     * @param content 投稿内容
     */
    public Post(String content) {
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isDraft = true;
    }

    /**
     * 投稿IDを取得します。
     *
     * @return 投稿ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 投稿IDを設定します。
     *
     * @param id 投稿ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 投稿内容を取得します。
     *
     * @return 投稿内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 投稿内容を設定します。
     * 内容を変更すると、更新日時も現在時刻に更新されます。
     *
     * @param content 投稿内容
     */
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    /**
     * 投稿の作成日時を取得します。
     *
     * @return 作成日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 投稿の作成日時を設定します。
     *
     * @param createdAt 作成日時
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 投稿の最終更新日時を取得します。
     *
     * @return 最終更新日時
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 投稿の最終更新日時を設定します。
     *
     * @param updatedAt 最終更新日時
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 投稿の公開日時を取得します。
     *
     * @return 公開日時、または未公開の場合はnull
     */
    public Instant getPublishedAt() {
        return publishedAt;
    }

    /**
     * 投稿の公開日時を設定します。
     *
     * @param publishedAt 公開日時
     */
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * 投稿が下書き状態かどうかを判定します。
     *
     * @return 下書き状態の場合はtrue、公開済みの場合はfalse
     */
    public boolean isDraft() {
        return isDraft;
    }

    /**
     * 投稿の下書き状態を設定します。
     * falseに設定された場合（公開状態）で、まだ公開日時が設定されていない場合は、
     * 現在時刻を公開日時として自動的に設定します。
     *
     * @param draft 下書き状態の場合はtrue、公開状態の場合はfalse
     */
    public void setDraft(boolean draft) {
        isDraft = draft;
        if (!draft && publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

    /**
     * 指定された検索条件に投稿がマッチするかどうかを判定します。
     * 下書き状態の投稿や、公開日時が設定されていない投稿は常にマッチしません。
     *
     * @param searchParams 検索条件パラメータ
     * @return 検索条件にマッチする場合はtrue、そうでない場合はfalse
     */
    public boolean matchesSearchCriteria(SearchParams searchParams) {
        if (this.isDraft || this.publishedAt == null) {
            return false;
        }
        
        if (searchParams == null) {
            return true;
        }
        
        if (searchParams.getContentKeyword() != null && !searchParams.getContentKeyword().isEmpty()) {
            if (this.content == null) {
                return false;
            }
            
            String contentLower = this.content.toLowerCase();
            String keywordLower = searchParams.getContentKeyword().toLowerCase();
            
            if (!contentLower.contains(keywordLower)) {
                return false;
            }
        }
        
        if (searchParams.getPublishedAfter() != null) {
            if (this.publishedAt.isBefore(searchParams.getPublishedAfter())) {
                return false;
            }
        }
        
        if (searchParams.getPublishedBefore() != null) {
            if (this.publishedAt.isAfter(searchParams.getPublishedBefore())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 投稿の検索条件を表す内部クラス。
     * 投稿内容のキーワード検索や、公開日時による範囲検索をサポートします。
     */
    public static class SearchParams {
        private String contentKeyword;
        private Instant publishedAfter;
        private Instant publishedBefore;
        
        /**
         * 検索キーワードを取得します。
         *
         * @return 投稿内容の検索キーワード
         */
        public String getContentKeyword() {
            return contentKeyword;
        }
        
        /**
         * 検索キーワードを設定します。
         *
         * @param contentKeyword 投稿内容の検索キーワード
         */
        public void setContentKeyword(String contentKeyword) {
            this.contentKeyword = contentKeyword;
        }
        
        /**
         * 公開日時の開始日時（この日時以降に公開された投稿を検索）を取得します。
         *
         * @return 公開日時の開始日時
         */
        public Instant getPublishedAfter() {
            return publishedAfter;
        }
        
        /**
         * 公開日時の開始日時を設定します。
         *
         * @param publishedAfter 公開日時の開始日時（この日時以降に公開された投稿を検索）
         */
        public void setPublishedAfter(Instant publishedAfter) {
            this.publishedAfter = publishedAfter;
        }
        
        /**
         * 公開日時の終了日時（この日時以前に公開された投稿を検索）を取得します。
         *
         * @return 公開日時の終了日時
         */
        public Instant getPublishedBefore() {
            return publishedBefore;
        }
        
        /**
         * 公開日時の終了日時を設定します。
         *
         * @param publishedBefore 公開日時の終了日時（この日時以前に公開された投稿を検索）
         */
        public void setPublishedBefore(Instant publishedBefore) {
            this.publishedBefore = publishedBefore;
        }
    }

    /**
     * このオブジェクトが指定されたオブジェクトと等しいかどうかを判定します。
     * 投稿IDが等しい場合に等価と判断されます。
     *
     * @param o 比較対象のオブジェクト
     * @return 等価の場合はtrue、そうでない場合はfalse
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;
        return id != null ? id.equals(post.id) : post.id == null;
    }

    /**
     * このオブジェクトのハッシュコードを返します。
     * 投稿IDに基づいてハッシュコードが計算されます。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * このオブジェクトの文字列表現を返します。
     *
     * @return オブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isDraft=" + isDraft +
                '}';
    }
}
