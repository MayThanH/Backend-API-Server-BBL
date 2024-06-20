package com.may.app.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.may.app.rest.entity.Post;
import com.may.app.rest.entity.User;
import com.may.app.rest.service.PostService;
import com.may.app.rest.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jane"));
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    public void testGetUserByIdUserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userController.getUserById(1L));

    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setName("Jane Doe");

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        when(userService.updateUser(any(User.class), eq(1L))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    public void testPatchUser() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Patched User");

        ObjectMapper objectMapper = new ObjectMapper();
        String updatesJson = objectMapper.writeValueAsString(updates);

        User patchedUser = new User();
        patchedUser.setId(1L);
        patchedUser.setName("Patched User");

        when(userService.patchUser(anyMap(), eq(1L))).thenReturn(patchedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Patched User"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFilterUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");

        when(userService.filterUsers(anyMap())).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/users/filter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jane"));
    }

    @Test
    public void testGetUserPosts() throws Exception {
        Long userId = 1L;
        Post post1 = new Post();
        post1.setId(userId);
        post1.setTitle("Title 1");
        post1.setContent("Content 2");
        Post post2 = new Post();
        post2.setId(userId);
        post2.setTitle("Title 2");
        post1.setContent("Content 2");

        List<Post> mockPosts = Arrays.asList(post1, post2);

        when(postService.getPostsByUserId(anyLong())).thenReturn(mockPosts);

        mockMvc.perform(get("/users/{userId}/posts", userId))
                .andExpect(status().isOk());

    }
}