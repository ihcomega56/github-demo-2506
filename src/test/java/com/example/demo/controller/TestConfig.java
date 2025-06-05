package com.example.demo.controller;

import com.example.demo.service.PostService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * テスト用の設定クラス
 * モックオブジェクトを提供する
 */
@Configuration
public class TestConfig {
    
    /**
     * PostServiceのモックインスタンスを提供する
     * @return モック化されたPostService
     */
    @Bean
    public PostService postService() {
        return Mockito.mock(PostService.class);
    }
}
