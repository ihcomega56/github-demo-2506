package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demoアプリケーションのメインクラス。
 * Spring Bootアプリケーションを起動するためのエントリーポイントです。
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * アプリケーションのメインメソッド。
     * Spring Bootアプリケーションを起動します。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
