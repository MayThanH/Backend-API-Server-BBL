package com.may.app.rest.repository;

import com.may.app.rest.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitle(String title);

    List<Post> findByContent(String content);

    List<Post> findByUserId(Long userId);

    List<Post> findByTitleAndContent(String title, String content);
}
