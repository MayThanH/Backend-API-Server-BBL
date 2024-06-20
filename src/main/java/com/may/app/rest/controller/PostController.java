package com.may.app.rest.controller;

import com.may.app.rest.entity.Post;
import com.may.app.rest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@RequestBody Post newPost, @PathVariable Long id) {
        return postService.updatePost(newPost, id);
    }

    @PatchMapping("/{id}")
    public Post patchPost(@RequestBody Post patchedPost, @PathVariable Long id) {
        return postService.patchPost(patchedPost, id);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/filter")
    public List<Post> filterPosts(@RequestParam Map<String, String> filters) {
        return postService.filterPosts(filters);
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }
}
