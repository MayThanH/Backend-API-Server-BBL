package com.may.app.rest.controller;

import com.may.app.rest.entity.Post;
import com.may.app.rest.entity.User;
import com.may.app.rest.service.UserService;
import com.may.app.rest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User newUser, @PathVariable Long id) {
        return userService.updateUser(newUser, id);
    }

    @PatchMapping("/{id}")
    public User patchUser(@RequestBody Map<String, Object> updates, @PathVariable Long id) {
        return userService.patchUser(updates, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/filter")
    public List<User> filterUsers(@RequestParam Map<String, String> filters) {
        return userService.filterUsers(filters);
    }

    @GetMapping("/{userId}/posts")
    public List<Post> getUserPosts(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }
}
