package com.example.ticketing.model.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRequest {
    private String content;
    private int rating;
}
