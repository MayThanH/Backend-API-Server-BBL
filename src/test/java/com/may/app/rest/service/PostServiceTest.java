package com.may.app.rest.service;

import com.may.app.rest.entity.Post;
import com.may.app.rest.entity.User;
import com.may.app.rest.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllPosts() {
        User user = new User();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findAll()).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.getAllPosts();

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findAll();
    }

    @Test
    public void testGetPostByIdPostExists() {
        Long postId = 1L;
        User user = new User();
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Title");
        post.setContent("Content");
        post.setUser(user);

        Optional<Post> mockPost = Optional.of(post);

        when(postRepository.findById(postId)).thenReturn(mockPost);

        Optional<Post> returnedPost = postService.getPostById(postId);

        assertTrue(returnedPost.isPresent());
        assertEquals("Title", returnedPost.get().getTitle());
        assertEquals("Content", returnedPost.get().getContent());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void testGetPostByIdPostNotExists() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Post> returnedPost = postService.getPostById(1L);

        assertTrue(returnedPost.isEmpty());

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreatePost() {
        User user = new User();
        Post newPost = new Post();
        newPost.setTitle("New Title");
        newPost.setContent("New Content");
        newPost.setUser(user);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle("New Title");
        savedPost.setContent("New Content");
        savedPost.setUser(user);

        when(postRepository.save(newPost)).thenReturn(savedPost);

        Post returnedPost = postService.createPost(newPost);

        assertNotNull(returnedPost.getId());
        assertEquals("New Title", returnedPost.getTitle());
        assertEquals("New Content", returnedPost.getContent());

        verify(postRepository, times(1)).save(newPost);
    }

    @Test
    public void testUpdatePost() {
        Long postId = 1L;
        User user = new User();

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");
        updatedPost.setUser(user);

        when(postRepository.save(updatedPost)).thenReturn(updatedPost);

        Post returnedPost = postService.updatePost(updatedPost, postId);

        assertEquals("Updated Title", returnedPost.getTitle());
        assertEquals("Updated Content", returnedPost.getContent());

        verify(postRepository, times(1)).save(updatedPost);
    }

    @Test
    public void testPatchPostPostExists() {
        Long postId = 1L;
        String patchedTitle = "Patched Title";
        String patchedContent = "Patched Content";

        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Existing Title");
        existingPost.setContent("Existing Content");

        Post patchedPost = new Post();
        patchedPost.setId(postId);
        patchedPost.setTitle(patchedTitle);
        patchedPost.setContent(patchedContent);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(patchedPost);

        Post returnedPost = postService.patchPost(patchedPost, postId);

        assertEquals(patchedTitle, returnedPost.getTitle());
        assertEquals(patchedContent, returnedPost.getContent());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(existingPost);
    }

    @Test
    public void testPatchPostPostNotFound() {
        Long postId = 1L;
        Post patchedPost = new Post();
        patchedPost.setId(postId);
        patchedPost.setTitle("Patched Title");
        patchedPost.setContent("Patched Content");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.patchPost(patchedPost, postId));

        verify(postRepository, times(1)).findById(postId);

        verify(postRepository, never()).save(any());
    }

    @Test
    public void testDeletePost() {
        postService.deletePost(1L);

        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testFilterPostsTitleAndContent() {
        Map<String, String> filters = new HashMap<>();
        User user = new User();

        filters.put("title", "Title");
        filters.put("content", "Content");

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findByTitleAndContent("Title", "Content")).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.filterPosts(filters);

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findByTitleAndContent("Title", "Content");
        verify(postRepository, never()).findByTitle(anyString());
        verify(postRepository, never()).findByContent(anyString());
        verify(postRepository, never()).findAll();
    }

    @Test
    public void testFilterPostsTitleOnly() {
        Map<String, String> filters = new HashMap<>();
        User user = new User();
        filters.put("title", "Title");

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findByTitle("Title")).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.filterPosts(filters);

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findByTitle("Title");
        verify(postRepository, never()).findByTitleAndContent(anyString(), anyString());
        verify(postRepository, never()).findByContent(anyString());
        verify(postRepository, never()).findAll();
    }

    @Test
    public void testFilterPostsContentOnly() {
        Map<String, String> filters = new HashMap<>();
        filters.put("content", "Content");
        User user = new User();

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findByContent("Content")).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.filterPosts(filters);

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findByContent("Content");
        verify(postRepository, never()).findByTitle(anyString());
        verify(postRepository, never()).findByTitleAndContent(anyString(), anyString());
        verify(postRepository, never()).findAll();
    }

    @Test
    public void testFilterPostsNoFilters() {
        Map<String, String> filters = new HashMap<>();
        User user = new User();

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findAll()).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.filterPosts(filters);

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findAll();
        verify(postRepository, never()).findByTitle(anyString());
        verify(postRepository, never()).findByContent(anyString());
        verify(postRepository, never()).findByTitleAndContent(anyString(), anyString());
    }

    @Test
    public void testGetPostsByUserId() {
        Long userId = 1L;
        User user = new User();

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setContent("Content 1");
        post1.setUser(user);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setContent("Content 2");
        post2.setUser(user);
        List<Post> mockPosts = Arrays.asList(
                post1,
                post2
        );

        when(postRepository.findByUserId(userId)).thenReturn(mockPosts);

        List<Post> returnedPosts = postService.getPostsByUserId(userId);

        assertEquals(2, returnedPosts.size());
        assertEquals("Title 1", returnedPosts.get(0).getTitle());
        assertEquals("Content 2", returnedPosts.get(1).getContent());

        verify(postRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetPostsByUserIdNoPostsFound() {
        Long userId = 1L;

        when(postRepository.findByUserId(userId)).thenReturn(Arrays.asList());

        List<Post> returnedPosts = postService.getPostsByUserId(userId);

        assertEquals(0, returnedPosts.size());

        verify(postRepository, times(1)).findByUserId(userId);
    }

}