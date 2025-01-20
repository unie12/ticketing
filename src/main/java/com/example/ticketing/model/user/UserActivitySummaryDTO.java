package com.example.ticketing.model.user;

import com.example.ticketing.model.favorite.FavoriteDTO;
import com.example.ticketing.model.review.ReviewResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserActivitySummaryDTO {
    private List<ReviewResponse> reviews;
    private List<FavoriteDTO> favorites;
}
