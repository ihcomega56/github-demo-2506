package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 投稿作成リクエストのデータ転送オブジェクト。
 * バリデーション機能を統一的に提供します。
 */
public class CreatePostRequest {
    
    @NotBlank(message = "投稿内容は必須です")
    @Size(max = 1000, message = "投稿内容は1000文字以内で入力してください")
    private String content;

    /**
     * デフォルトコンストラクタ。
     */
    public CreatePostRequest() {
    }

    /**
     * 投稿内容を指定するコンストラクタ。
     *
     * @param content 投稿内容
     */
    public CreatePostRequest(String content) {
        this.content = content;
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
     *
     * @param content 投稿内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}
