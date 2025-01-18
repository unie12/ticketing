package com.example.ticketing.repository.review;

import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);

    List<Review> findByStore(Store store);

    boolean existsByUserAndStore(User user, Store store);

    Page<Review> findByUser(User user, Pageable pageable);
    Page<Review> findByStore(Store store, Pageable pageable);
}
