package com.example.ticketing.service.heart;

import com.example.ticketing.model.heart.Heart;
import com.example.ticketing.model.heart.HeartDTO;
import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.heart.HeartRepository;
import com.example.ticketing.service.review.ReviewService;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService{
    private final HeartRepository heartRepository;
    private final UserService userService;
    private final ReviewService reviewService;

    @Override
    @Transactional
    public HeartDTO toggleHeart(Long reviewId, Long userId) {
        User user = userService.findUserById(userId);
        Review review = reviewService.findReviewById(reviewId);

        Optional<Heart> existingHeart = heartRepository.findByUserAndReview(user, review);
        if (existingHeart.isPresent()) {
            heartRepository.delete(existingHeart.get());
            review.decrementHeartCount();
            return null;
        } else {
            Heart heart = Heart.builder()
                    .user(user)
                    .review(review)
                    .build();
            review.incrementHeartCount();
            HeartDTO dto = HeartDTO.from(heartRepository.save(heart));
            return dto;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HeartDTO> getMyHearts(Long userId) {
        User user = userService.findUserById(userId);
        List<Heart> hearts = heartRepository.findByUser(user);
        return hearts.stream()
                .map(HeartDTO::from)
                .collect(Collectors.toList());
    }
}
