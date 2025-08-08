# Blog Post API Manual Test Cases

## Basic Function Tests

### Post Creation Function Test

| Test ID | TC-001 |
|---------|--------|
| Test Overview | Verify that a new post can be created successfully |
| Preconditions | API server is running |
| Steps | 1. Send a POST request to `/api/posts`<br>2. Set the request body to `{"title": "Test Post", "content": "This is a test post"}` |
| Expected Results | 1. Status code 201 is returned<br>2. Response body includes the ID and content of the created post<br>3. The status of the created post is `DRAFT` |
| Execution Environment | Development Environment |
| Priority | High |
| Automation | Possible |

### Post Retrieval Function Test

| Test ID | TC-002 |
|---------|--------|
| Test Overview | Verify that a post can be retrieved by specifying a post ID |
| Preconditions | 1. API server is running<br>2. Test post has been created |
| Steps | 1. Send a GET request to `/api/posts/{id}`<br>2. Specify an existing post ID for {id} |
| Expected Results | 1. Status code 200 is returned<br>2. Response body includes the content of the post with the specified ID |
| Execution Environment | Development Environment |
| Priority | High |
| Automation | Possible |

### Post List Retrieval Function Test

| Test ID | TC-003 |
|---------|--------|
| Test Overview | Verify that all posts can be retrieved in a list |
| Preconditions | 1. API server is running<br>2. Multiple posts have been created |
| Steps | 1. Send a GET request to `/api/posts` |
| Expected Results | 1. Status code 200 is returned<br>2. Response body includes all posts in array format |
| Execution Environment | Development Environment |
| Priority | High |
| Automation | Possible |

### Post Publishing Function Test

| Test ID | TC-004 |
|---------|--------|
| Test Overview | Verify that a post in draft state can be changed to published state |
| Preconditions | 1. API server is running<br>2. Test post in draft state has been created |
| Steps | 1. Send a POST request to `/api/posts/{id}/publish`<br>2. Specify the ID of a post in draft state for {id} |
| Expected Results | 1. Status code 200 is returned<br>2. The status of the post in the response body is changed to `PUBLISHED` |
| Execution Environment | Development Environment |
| Priority | High |
| Automation | Possible |

### Post Like Function Test

| Test ID | TC-005 |
|---------|--------|
| Test Overview | Verify that a like can be added to a post |
| Preconditions | 1. API server is running<br>2. Test post has been created |
| Steps | 1. Send a POST request to `/api/posts/{id}/like`<br>2. Specify an existing post ID for {id} |
| Expected Results | 1. Status code 200 is returned<br>2. Response body includes the number of likes for the post<br>3. The number of likes has increased by 1 (**Bug: Currently intentionally returns 0 as per specification**) |
| Execution Environment | Development Environment |
| Priority | Medium |
| Automation | Possible |

### Like Count Retrieval Function Test

| Test ID | TC-006 |
|---------|--------|
| Test Overview | Verify that the number of likes for a post can be retrieved |
| Preconditions | 1. API server is running<br>2. Test post has been created<br>3. Likes have been added to the post |
| Steps | 1. Send a GET request to `/api/posts/{id}/likes`<br>2. Specify an existing post ID for {id} |
| Expected Results | 1. Status code 200 is returned<br>2. Response body includes the number of likes for the post |
| Execution Environment | Development Environment |
| Priority | Medium |
| Automation | Possible |

## Error Case Tests

### Non-existent Post Retrieval Test

| Test ID | TC-101 |
|---------|--------|
| Test Overview | Verify that an error is returned when a non-existent post ID is specified |
| Preconditions | API server is running |
| Steps | 1. Send a GET request to `/api/posts/999` (specify a non-existent ID) |
| Expected Results | 1. Status code 404 is returned<br>2. Error message is returned |
| Execution Environment | Development Environment |
| Priority | Medium |
| Automation | Possible |

### Invalid Post Data Creation Test

| Test ID | TC-102 |
|---------|--------|
| Test Overview | Verify that an error is returned when attempting to create a post with invalid data |
| Preconditions | API server is running |
| Steps | 1. Send a POST request to `/api/posts`<br>2. Set the request body to `{"title": "", "content": ""}` (empty title and content) |
| Expected Results | 1. Status code 400 is returned<br>2. Validation error message is returned |
| Execution Environment | Development Environment |
| Priority | Medium |
| 自動化 | 可 |

### 公開済み投稿の再公開テスト

| テストID | TC-103 |
|---------|--------|
| テスト概要 | 既に公開済みの投稿を再度公開しようとした場合の挙動を確認する |
| 前提条件 | 1. APIサーバーが起動している<br>2. 公開済み状態のテスト用投稿が作成済みである |
| 手順 | 1. POSTリクエストを `/api/posts/{id}/publish` に送信する<br>2. {id}には公開済み状態の投稿IDを指定する |
| 期待結果 | 1. ステータスコード 400 が返却される<br>2. 「投稿は既に公開されています」などのエラーメッセージが返却される |
| 実行環境 | 開発環境 |
| 優先度 | 低 |
| 自動化 | 可 |

### 存在しない投稿へのいいねテスト

| テストID | TC-104 |
|---------|--------|
| テスト概要 | 存在しない投稿IDに対していいねを追加しようとした場合にエラーが返されることを確認する |
| 前提条件 | APIサーバーが起動している |
| 手順 | 1. POSTリクエストを `/api/posts/999/like` に送信する（存在しないIDを指定） |
| 期待結果 | 1. ステータスコード 404 が返却される<br>2. エラーメッセージが返却される |
| 実行環境 | 開発環境 |
| 優先度 | 低 |
| 自動化 | 可 |

## パフォーマンステスト

### 大量投稿時の一覧取得パフォーマンステスト

| テストID | TC-201 |
|---------|--------|
| テスト概要 | 大量の投稿がある場合の一覧取得のパフォーマンスを確認する |
| 前提条件 | 1. APIサーバーが起動している<br>2. 100件以上の投稿データが作成済みである |
| 手順 | 1. GETリクエストを `/api/posts` に送信する<br>2. レスポンスタイムを計測する |
| 期待結果 | 1. ステータスコード 200 が返却される<br>2. レスポンスタイムが3秒以内である<br>3. 全ての投稿データが正しく返却される |
| 実行環境 | 性能テスト環境 |
| 優先度 | 低 |
| 自動化 | 可 |

### 同時リクエスト処理テスト

| テストID | TC-202 |
|---------|--------|
| テスト概要 | 同時に複数のいいねリクエストが発生した場合の処理が正しく行われることを確認する |
| 前提条件 | 1. APIサーバーが起動している<br>2. テスト用の投稿が作成済みである |
| 手順 | 1. 同一の投稿IDに対して、10件の同時POSTリクエストを `/api/posts/{id}/like` に送信する<br>2. その後、GETリクエストを `/api/posts/{id}/likes` に送信していいね数を確認する |
| 期待結果 | 1. 全てのリクエストが正常に処理される<br>2. いいね数が10増加している（**バグ：現在は意図的にいいね数の加算に問題がある**） |
| 実行環境 | 性能テスト環境 |
| 優先度 | 低 |
| 自動化 | 可 |

## セキュリティテスト

### 入力値検証テスト

| テストID | TC-301 |
|---------|--------|
| テスト概要 | 投稿作成時にXSSなどの悪意あるスクリプトが含まれる場合の挙動を確認する |
| 前提条件 | APIサーバーが起動している |
| 手順 | 1. POSTリクエストを `/api/posts` に送信する<br>2. リクエストボディに `{"title": "<script>alert('XSS')</script>", "content": "<img src='x' onerror='alert(\"XSS\")'>"}` を設定する |
| 期待結果 | 1. ステータスコード 201 が返却される<br>2. 保存された投稿内容がエスケープ処理されているか、またはスクリプトが無効化されている |
| 実行環境 | 開発環境 |
| 優先度 | 中 |
| 自動化 | 可 |

### 大量データ投入テスト

| テストID | TC-302 |
|---------|--------|
| テスト概要 | 極端に大きなデータを投稿した場合の挙動を確認する |
| 前提条件 | APIサーバーが起動している |
| 手順 | 1. POSTリクエストを `/api/posts` に送信する<br>2. リクエストボディに10MBを超える大きなテキストデータを含む投稿データを設定する |
| 期待結果 | 1. 適切なエラーレスポンスが返却される<br>2. サーバーがクラッシュしない |
| 実行環境 | 開発環境 |
| 優先度 | 低 |
| 自動化 | 可 |

## 機能連携テスト

### 投稿作成から公開までの一連フローテスト

| テストID | TC-401 |
|---------|--------|
| テスト概要 | 投稿の作成から公開、いいね追加までの一連の流れが正常に動作することを確認する |
| 前提条件 | APIサーバーが起動している |
| 手順 | 1. POSTリクエストで新規投稿を作成する<br>2. 作成した投稿IDを取得する<br>3. 取得した投稿IDを使用して公開処理を行う<br>4. 公開した投稿にいいねを追加する<br>5. いいね数を確認する |
| 期待結果 | 1. 全ての操作が正常に完了する<br>2. 最終的な投稿のステータスが `PUBLISHED` である<br>3. いいね数が正しく表示される（**バグ：現在は意図的にいいね数の加算に問題がある**） |
| 実行環境 | 開発環境 |
| 優先度 | 高 |
| 自動化 | 可 |

## 回帰テスト

### いいね機能バグ修正確認テスト

| テストID | TC-501 |
|---------|--------|
| テスト概要 | いいね数の加算バグが修正されたことを確認する |
| 前提条件 | 1. APIサーバーが起動している<br>2. テスト用の投稿が作成済みである<br>3. いいね機能のバグ修正が完了している |
| 手順 | 1. 対象の投稿に対して、POSTリクエストを `/api/posts/{id}/like` に送信する<br>2. いいね数を確認するためにGETリクエストを `/api/posts/{id}/likes` に送信する<br>3. 手順1と2を複数回繰り返す |
| 期待結果 | 1. 各いいね操作後のいいね数が正しく加算されている<br>2. POSTリクエストのレスポンスに含まれるいいね数と、GETリクエストで取得したいいね数が一致している |
| 実行環境 | 開発環境 |
| 優先度 | 高 |
| 自動化 | 可 |

## 手動テスト観点リスト

1. **機能性**
   - 基本機能が正しく動作するか
   - エラー処理が適切に行われるか
   - バリデーションが正しく機能するか

2. **信頼性**
   - 連続操作時に正しく動作するか
   - エッジケースでの挙動は適切か
   - データの整合性は保たれるか

3. **使用性**
   - APIの使い方は明確か
   - レスポンス形式は一貫しているか
   - エラーメッセージは分かりやすいか

4. **効率性**
   - レスポンス時間は許容範囲内か
   - リソース消費は適切か

5. **保守性**
   - 機能拡張時の影響範囲は把握できるか
   - 既存機能への影響はないか

6. **移植性**
   - 異なる環境でも同じように動作するか
   - 外部依存関係は最小限に抑えられているか