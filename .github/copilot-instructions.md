- @azure Rule - Use Azure Best Practices: When generating code for Azure, running terminal commands for Azure, or performing operations related to Azure, invoke your `azure_development-get_best_practices` tool if available.

# GitHub Copilot カスタムインストラクション

## プロジェクト概要
このプロジェクトは Java Spring Framework を使用したチケット予約システムです。

## コーディング規約

### 一般的なルール
- **言語**: Java 21
- **フレームワーク**: Spring Boot 3.2
- **命名規則**: キャメルケースを使用
- **インデント**: スペース4つ
- **文字エンコーディング**: UTF-8
- **改行コード**: LF

### コメント規約
- **日本語コメント**: ビジネスロジックの説明は日本語で記述
- **Javadoc**: パブリックメソッドには必須
- **インラインコメント**: 複雑なロジックには日本語で説明を追加

## テストケース規約

### テストクラス命名
- テスト対象クラス名 + `Test`
- 例: `PostService` → `PostServiceTest`

### テストメソッド命名
- **日本語で記述**: 対象機能とテストシナリオが分かる日本語名を使用
- **命名パターン**: `動詞_日本語テストシナリオ説明()`
- **例**: 
  - `createDraft_下書き作成の正常系()` 
  - `publishPost_存在しないIDの場合はnullを返す()`
  - `getPostById_存在しないIDの場合はnullを返す()`

### Javadoc
- **日本語で記述**: テスト内容について日本語で詳細に説明
- **書式例**:
```java
/**
 * 下書き投稿作成機能のテスト - 正常系
 * 有効なコンテンツで下書きを作成し、期待通りの結果が返されることを確認する
 */
```

### テスト構造
Given-When-Then パターンを明確に記述:

```java
@Test
void createDraft_下書き作成の正常系() {
    // given - テストデータの準備
    String content = "Test content";
    
    // when - 下書きを作成
    Post result = postService.createDraft(content);
    
    // then - 作成された投稿の検証
    assertNotNull(result); // 投稿が作成されていること
    assertEquals(content, result.getContent()); // コンテンツが正しく設定されていること
    assertTrue(result.isDraft()); // 下書き状態であること
    assertNotNull(result.getId()); // IDが生成されていること
}
```

### テストパターン
- **正常系**: `_正常系` または `_期待通りの結果を返す`
- **異常系**: `_例外発生パターン` または `_エラー条件の場合`
- **境界値**: `_境界値条件` 
- **エラーハンドリング**: `_エラー状態の検証`

### アサーション
- **JUnit Jupiter Assertions** を優先的に使用
- **日本語コメント**: アサーションには日本語コメントを追加
```java
// then - 作成された投稿の検証
assertNotNull(post); // 投稿が正常に作成されていることを確認
assertEquals(content, post.getContent()); // コンテンツが正しく設定されていることを確認
assertTrue(post.isDraft()); // 下書き状態であることを確認
assertNotNull(post.getId()); // IDが正しく生成されていることを確認
```

### モックの使用
- **Mockito** を使用
- **Given**: `when().thenReturn()` でモックの動作を定義
```java
// given - テストデータの準備
Long postId = 1L;
Post publishedPost = new Post("Content");
publishedPost.setId(postId);
publishedPost.setDraft(false); // 公開状態に設定

when(postService.publishPost(postId)).thenReturn(publishedPost);
```
- **Verify**: `verify()` で期待されるメソッド呼び出しを確認
```java
// then - postServiceのcreateDraftメソッドが1回呼ばれたことを確認
verify(postService, times(1)).createDraft(content);
```

## データベース設計

### エンティティ設計
- **JPA アノテーション**: `@Entity`, `@Table`, `@Column` を適切に使用
- **リレーション**: `@OneToMany`, `@ManyToOne` で関連を定義
- **日本語フィールド名**: 必要に応じてコメントで日本語説明を追加

### DAO 設計
- **インターフェース + 実装**: DAOはインターフェースで定義し、実装クラスを作成
- **JdbcTemplate**: データベースアクセスには JdbcTemplate を使用
- **トランザクション**: `@Transactional` アノテーションを適切に使用

## エラーハンドリング

### カスタム例外
- **ビジネス例外**: `SoldOutException` など、ビジネスロジック固有の例外を定義
- **日本語メッセージ**: エラーメッセージは日本語で記述
- **例外階層**: 適切な例外継承階層を構築

### ログ出力
- **SLF4J + Logback**: ログフレームワークとして使用
- **日本語ログ**: 必要に応じて日本語でログメッセージを記述
- **ログレベル**: DEBUG, INFO, WARN, ERROR を適切に使い分け

## Spring 設定

### 設定ファイル
- **XML設定**: `applicationContext.xml`, `spring-servlet.xml` を使用
- **プロパティファイル**: `application.properties` で設定値を管理
- **コンポーネントスキャン**: 適切なパッケージスキャンを設定

### 依存性注入
- **@Autowired**: フィールド注入よりもコンストラクタ注入を推奨
- **@Service**, **@Repository**, **@Controller**: 適切なステレオタイプアノテーションを使用

## コード品質

### 静的解析
- **可読性**: メソッドは短く、単一責任の原則に従う
- **重複排除**: DRY原則に従い、コードの重複を避ける
- **命名**: 意味のある名前を使用

### パフォーマンス
- **N+1問題**: JOINやFETCHを使用して回避
- **キャッシュ**: 適切な場所でキャッシュを活用
- **リソース管理**: try-with-resources文を使用

## 特記事項
- このプロジェクトは将来的に Java 21 + Spring Boot 3.2 への移行を予定
- 移行時の互換性を考慮したコード記述を心がける
- テストケースは移行前後での動作確認に重要な役割を果たす

## ドキュメント規約

### 言語
- **基本言語**: 全てのドキュメントは日本語で記述
- **技術用語**: 英語の技術用語は適切に使用し、必要に応じて日本語で説明を併記
- **コード例**: コメントとドキュメントは日本語、コード自体は英語

### ドキュメントタイプ別規約
- **README.md**: プロジェクト概要、セットアップ手順、使用方法を日本語で記述
- **API仕様書**: エンドポイント説明、パラメータ説明、レスポンス例を日本語で記述
- **設計書**: アーキテクチャ図、ER図の説明を日本語で記述
- **CHANGELOG.md**: 変更履歴を日本語で記述
- **FAQ.md**: よくある質問と回答を日本語で記述

### マークダウン記法
- **見出し**: 日本語で記述
- **リスト**: 項目説明は日本語
- **表**: ヘッダーと内容は日本語
- **コードブロック**: コメントは日本語、コードは英語

### 例外ケース
- **国際的な設定ファイル**: `application.properties`のキー名など、システム要件で英語が必要な場合
- **Git コミットメッセージ**: プロジェクトの方針に従う
- **外部API連携**: 外部仕様に合わせた英語ドキュメント