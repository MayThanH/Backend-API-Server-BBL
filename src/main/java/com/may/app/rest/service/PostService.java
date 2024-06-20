package com.may.app.rest.service;

import com.may.app.rest.entity.Post;
import com.may.app.rest.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Post newPost, Long id) {
        newPost.setId(id);
        return postRepository.save(newPost);
    }

    public Post patchPost(Post patchedPost, Long id) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            // Patch only non-null fields
            if (patchedPost.getTitle() != null) {
                post.setTitle(patchedPost.getTitle());
            }
            if (patchedPost.getContent() != null) {
                post.setContent(patchedPost.getContent());
            }
            // Apply more patches as needed
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found with id: " + id);
        }
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<Post> filterPosts(Map<String, String> filters) {
        String title = filters.get("title");
        String content = filters.get("content");

        if (title != null && content != null) {
            return postRepository.findByTitleAndContent(title, content);
        } else if (title != null) {
            return postRepository.findByTitle(title);
        } else if (content != null) {
            return postRepository.findByContent(content);
        } else {
            return getAllPosts();
        }
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }
}