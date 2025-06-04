## 仕様

### クラス図
```mermaid
classDiagram
    class Post {
        Long id
        String content
        Instant createdAt
        Instant updatedAt
        Instant publishedAt
        boolean isDraft
        +getId() Long
        +setId(Long id) void
        +getContent() String
        +setContent(String content) void
        +isDraft() boolean
        +setDraft(boolean isDraft) void
        +getPublishedAt() Instant
        +setPublishedAt(Instant publishedAt) void
    }
    
    class PostService {
        -Map~Long, Post~ posts
        -AtomicLong idGenerator
        +createDraft(String content) Post
        +publishPost(Long id) Post
        +deletePost(Long id) boolean
        +getPost(Long id) Post
        +getPublishedPosts() List~Post~
        +getDraftPosts() List~Post~
        +searchPosts(SearchParams params) List~Post~
    }
    
    class PostController {
        -PostService postService
        +createDraft(Map payload) ResponseEntity~Post~
        +publishPost(Long id) ResponseEntity~Post~
        +getPost(Long id) ResponseEntity~Post~
        +deletePost(Long id) ResponseEntity~void~
        +getPublishedPosts() ResponseEntity~List~Post~~
        +getDraftPosts() ResponseEntity~List~Post~~
    }
    
    class SearchParams {
        String keyword
        Boolean isDraft
        Instant publishedAfter
        Instant publishedBefore
    }
    
    PostController --> PostService : 依存
    PostService --> Post : 使用
    Post *-- SearchParams : 内部クラス

```

### API構造図
```mermaid
graph TD
    Client([クライアント]) -->|リクエスト| API[API Layer\nPostController]
    API -->|処理委譲| Service[Service Layer\nPostService]
    Service -->|データ操作| Model[Model Layer\nPost]
    Model -->|保存| DB[(インメモリ\nストレージ)]
    
    API -->|レスポンス| Client
```

### リクエストフロー図
```mermaid
sequenceDiagram
    participant Client as クライアント
    participant Controller as PostController
    participant Service as PostService
    participant Model as Post
    
    Client->>Controller: POST /api/posts/drafts
    Controller->>Service: createDraft(content)
    Service->>Model: new Post(content)
    Service->>Service: posts.put(post.getId(), post)
    Service-->>Controller: 作成されたPost
    Controller-->>Client: HTTP 201 Created + Post JSON
    
    Client->>Controller: PUT /api/posts/drafts/{id}/publish
    Controller->>Service: publishPost(id)
    Service->>Model: post.setDraft(false)
    Service->>Model: post.setPublishedAt(now)
    Service-->>Controller: 公開されたPost
    Controller-->>Client: HTTP 200 OK + Post JSON
```

## 実行方法

### 必要な環境

- JDK 21以上
- 【任意】 Gradle 8.4以上 ※Gradle Wrapperでもかまいません。サンプルコマンドでも使用しています。

### 実行方法

1. ハンズオンディレクトリに移動します。
    ```bash
    cd /workspaces/github-demo-2506/demo/
    ```

2. 必要な依存関係をインストールします。
    ```bash
    ./gradlew build
    ```

3. アプリケーションを起動します。
    ```bash
    ./gradlew bootRun
    ```

4. アプリケーションが起動したら、以下のURLでAPIを利用できます。

---

## API実行のサンプルcurlコマンド

### 1. 下書き投稿の作成
```bash
curl -X POST http://localhost:8080/api/posts/drafts \
-H "Content-Type: application/json" \
-d '{"content": "This is a draft post."}'
```

### 2. 下書き投稿の公開
```bash
curl -X PUT http://localhost:8080/api/posts/drafts/{id}/publish
```

### 3. 投稿の取得
```bash
curl -X GET http://localhost:8080/api/posts/{id}
```

### 4. 投稿の削除
```bash
curl -X DELETE http://localhost:8080/api/posts/{id}
```

### 5. 公開済み投稿の一覧取得
```bash
curl -X GET http://localhost:8080/api/posts/published
```

### 6. 下書き投稿の一覧取得
```bash
curl -X GET http://localhost:8080/api/posts/drafts
```

