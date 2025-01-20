package com.example.ticketing.repository.heart;

import com.example.ticketing.model.heart.Heart;
import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByUserAndReview(User user, Review review);

    List<Heart> findByUser(User user);
}
