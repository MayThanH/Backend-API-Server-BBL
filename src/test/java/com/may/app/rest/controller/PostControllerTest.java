package com.may.app.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.may.app.rest.entity.Post;
import com.may.app.rest.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    public void testGetAllPosts() throws Exception {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postService.getAllPosts()).thenReturn(mockPosts);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Title 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    public void testGetPostById() throws Exception {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Post");
        post.setContent("Test Content");

        when(postService.getPostById(postId)).thenReturn(Optional.of(post));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Post"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Test Content"));
    }

    @Test
    public void testCreatePost() throws Exception {
        Post postToCreate = new Post();
        postToCreate.setTitle("New Post");
        postToCreate.setContent("New Content");
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle("New Post");
        savedPost.setContent("New Content");
        when(postService.createPost(any(Post.class))).thenReturn(savedPost);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String postJson = objectMapper.writeValueAsString(postToCreate);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New Post"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("New Content"));
    }

    @Test
    public void testUpdatePost() throws Exception {
        Long postId = 1L;
        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        when(postService.updatePost(any(Post.class), eq(postId))).thenReturn(updatedPost);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedPostJson = objectMapper.writeValueAsString(updatedPost);

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPostJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Updated Content"));
    }

    @Test
    public void testPatchPost() throws Exception {
        Long postId = 1L;
        Post patchedPost = new Post();
        patchedPost.setId(postId);
        patchedPost.setTitle("Patched Title");

        when(postService.patchPost(any(Post.class), eq(postId))).thenReturn(patchedPost);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String patchedPostJson = objectMapper.writeValueAsString(patchedPost);

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchedPostJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Patched Title"));
    }

    @Test
    public void testDeletePost() throws Exception {
        Long postId = 1L;

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{id}", postId))
                .andExpect(status().isOk());

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    public void testFilterPosts() throws Exception {
        Map<String, String> filters = new HashMap<>();
        filters.put("title", "Test");

        Post filteredPost = new Post();
        filteredPost.setId(1L);
        filteredPost.setTitle("Test Post");
        filteredPost.setContent("Test Content");

        when(postService.filterPosts(anyMap())).thenReturn(Collections.singletonList(filteredPost));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/filter")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Post"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value("Test Content"));
    }

    @Test
    public void testGetPostsByUser() throws Exception {
        Long userId = 1L;
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");

        List<Post> postsByUser = Arrays.asList(
                post1,
                post2
        );

        when(postService.getPostsByUserId(userId)).thenReturn(postsByUser);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Title 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Title 2"));
    }
}