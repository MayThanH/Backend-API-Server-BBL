package com.may.app.rest.service;

import com.may.app.rest.entity.User;
import com.may.app.rest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userService).build();
    }

    @Test
    public void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Jane");
        user1.setEmail("jane@mail.com");
        userList.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@mail.com");
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> returnedUsers = userService.getAllUsers();

        assertEquals(2, returnedUsers.size());
        assertEquals(userList.get(0).getName(), returnedUsers.get(0).getName());
        assertEquals(userList.get(1).getEmail(), returnedUsers.get(1).getEmail());
    }

    @Test
    public void testGetUserByIdUserFound() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");
        user.setEmail("jane@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> returnedUser = userService.getUserById(1L);

        assertTrue(returnedUser.isPresent());
        assertEquals(user.getId(), returnedUser.get().getId());
        assertEquals(user.getName(), returnedUser.get().getName());
        assertEquals(user.getEmail(), returnedUser.get().getEmail());
    }

    @Test
    public void testGetUserByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> user = userService.getUserById(1L);

        assertFalse(user.isPresent());
    }

    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setName("Jane");
        newUser.setEmail("jane@mail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Jane");
        savedUser.setEmail("jane@mail.com");

        when(userRepository.save(newUser)).thenReturn(savedUser);

        User returnedUser = userService.createUser(newUser);

        assertEquals(savedUser.getId(), returnedUser.getId());
        assertEquals(savedUser.getName(), returnedUser.getName());
        assertEquals(savedUser.getEmail(), returnedUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("Jane");
        newUser.setEmail("jane@mail.com");

        when(userRepository.save(newUser)).thenReturn(newUser);

        User updatedUser = userService.updateUser(newUser, 1L);

        assertEquals(newUser.getId(), updatedUser.getId());
        assertEquals(newUser.getName(), updatedUser.getName());
        assertEquals(newUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testPatchUserUserFoundAndUpdated() {
        User existingUser = new User(1L, "John Doe", "johndoe", "john.doe@mail.com", null, "1234567890", "example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "jane.doe@mail.com");
        updates.put("phone", "9876543210");

        // Mock ReflectionUtils.setField
        updates.forEach((key, value) -> {
            ReflectionUtils.FieldCallback fieldCallback = field -> {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, existingUser, value);
            };
            ReflectionUtils.doWithFields(User.class, fieldCallback, field -> field.getName().equals(key));
        });

        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.patchUser(updates, 1L);

        assertEquals(1L, updatedUser.getId());
        assertEquals("John Doe", updatedUser.getName()); // Name should not change
        assertEquals("jane.doe@mail.com", updatedUser.getEmail());
        assertEquals("9876543210", updatedUser.getPhone());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existingUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testPatchUserUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "jane.doe@mail.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.patchUser(updates, 2L);
        });

        assertEquals("User not found with id: 2", exception.getMessage());

        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void testFilterUsers() {
        MockitoAnnotations.openMocks(this);

        Map<String, String> filters = new HashMap<>();
        filters.put("name", "Jane");
        filters.put("email", "mail.com");

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<User> criteriaQuery = mock(CriteriaQuery.class);
        Root<User> root = mock(Root.class);
        Predicate predicateName = mock(Predicate.class);
        Predicate predicateEmail = mock(Predicate.class);
        TypedQuery<User> typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(criteriaBuilder.like(root.get("name"), "%Jane%")).thenReturn(predicateName);
        when(criteriaBuilder.like(root.get("email"), "%mail.com%")).thenReturn(predicateEmail);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(predicateName);
        predicates.add(predicateEmail);
        when(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).thenReturn(predicateName);

        when(criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        List<User> returnedUsers = userService.filterUsers(filters);

        assertEquals(0, returnedUsers.size());
    }

}