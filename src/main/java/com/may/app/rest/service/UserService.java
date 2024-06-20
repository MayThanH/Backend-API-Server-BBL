package com.may.app.rest.service;

import com.may.app.rest.entity.User;
import com.may.app.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User newUser, Long id) {
        newUser.setId(id); // Ensure the new user object has the correct ID set
        return userRepository.save(newUser);
    }

    public User patchUser(Map<String, Object> updates, Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            updates.forEach((key, value) -> {
                ReflectionUtils.setField(Objects.requireNonNull(ReflectionUtils.findField(User.class, key)), user, value);
            });
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> filterUsers(Map<String, String> filters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "name", "username", "email", "phone", "website":
                        predicates.add(builder.like(root.get(key), "%" + value + "%"));
                        break;
                    case "address.street":
                        predicates.add(builder.like(root.get("address").get("street"), "%" + value + "%"));
                        break;
                    case "address.suite":
                        predicates.add(builder.like(root.get("address").get("suite"), "%" + value + "%"));
                        break;
                    case "address.city":
                        predicates.add(builder.like(root.get("address").get("city"), "%" + value + "%"));
                        break;
                    case "address.zipcode":
                        predicates.add(builder.like(root.get("address").get("zipcode"), "%" + value + "%"));
                        break;
                    case "company.name":
                        predicates.add(builder.like(root.get("company").get("name"), "%" + value + "%"));
                        break;
                    case "company.catchPhrase":
                        predicates.add(builder.like(root.get("company").get("catchPhrase"), "%" + value + "%"));
                        break;
                    case "company.bs":
                        predicates.add(builder.like(root.get("company").get("bs"), "%" + value + "%"));
                        break;
                }
            }
        });

        criteriaQuery.where(builder.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
