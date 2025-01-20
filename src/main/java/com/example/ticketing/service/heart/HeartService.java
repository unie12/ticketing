package com.example.ticketing.service.heart;

import com.example.ticketing.model.heart.HeartDTO;

import java.util.List;

public interface HeartService {
    HeartDTO toggleHeart(Long reviewId, Long userId);

    List<HeartDTO> getMyHearts(Long userId);
}
