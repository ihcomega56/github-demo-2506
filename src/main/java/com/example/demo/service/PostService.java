package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Post;

/**
 * 投稿に関するビジネスロジックを提供するサービスクラス。
 * 投稿の作成、公開、削除、取得など、投稿データの操作を担当します。
 * このデモ実装では、インメモリストレージを使用しています。
 */
@Service
public class PostService {
    // デモ用にシンプルなインメモリストレージを使用しています
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    /**
     * 指定された内容で下書き投稿を作成します。
     * 
     * @param content 投稿内容
     * @return 作成された投稿エンティティ
     */
    public Post createDraft(String content) {
        Post post = new Post(content);
        post.setId(idGenerator.getAndIncrement());
        posts.put(post.getId(), post);
        return post;
    }
    
    /**
     * 指定されたIDの下書き投稿を公開状態に変更します。
     * 
     * @param id 公開する投稿のID
     * @return 公開された投稿、または投稿が見つからない/既に公開済みの場合はnull
     */
    public Post publishPost(Long id) {
        Post post = posts.get(id);
        if (post != null && post.isDraft()) {
            post.setDraft(false);
            post.setPublishedAt(new Date().toInstant());
            return post;
        }
        return null;
    }
    
    /**
     * 指定されたIDの投稿を削除します。
     * 
     * @param id 削除する投稿のID
     * @return 削除が成功した場合はtrue、投稿が見つからない場合はfalse
     */
    public boolean deletePost(Long id) {
        return posts.remove(id) != null;
    }
    
    /**
     * 指定されたIDの投稿を取得します。
     * 
     * @param id 取得する投稿のID
     * @return 投稿エンティティ、または投稿が見つからない場合はnull
     */
    public Post getPost(Long id) {
        return posts.get(id);
    }
    
    /**
     * 公開済みの全投稿を取得します。
     * 
     * @return 公開済み投稿のリスト
     */
    public List<Post> getPublishedPosts() {
        return posts.values().stream()
                .filter(post -> !post.isDraft())
                .collect(Collectors.toList());
    }
    
    /**
     * 下書き状態の全投稿を取得します。
     * 
     * @return 下書き投稿のリスト
     */
    public List<Post> getDraftPosts() {
        return posts.values().stream()
                .filter(Post::isDraft)
                .collect(Collectors.toList());
    }
    
    /**
     * 指定されたIDの投稿にいいねを追加します。
     * 
     * @param id いいねする投稿のID
     * @return いいねが追加された投稿、または投稿が見つからない場合はnull
     */
    public Post likePost(Long id) {
        Post post = posts.get(id);
        if (post != null) {
            post.incrementLikes();
            return post;
        }
        return null;
    }
    
    /**
     * 指定されたIDの投稿のいいね数を取得します。
     * 
     * @param id いいね数を取得する投稿のID
     * @return いいね数、または投稿が見つからない場合はnull
     */
    public Integer getPostLikes(Long id) {
        Post post = posts.get(id);
        if (post != null) {
            return post.getLikes();
        }
        return null;
    }
}
