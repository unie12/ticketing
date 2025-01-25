package com.example.ticketing.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewActivityEvent extends UserActivityEvent {
    private Long reviewId;
    private Integer rating;
    private String categoryGroupCode;
//    private List<Long> categoryIds;
//    private VisitInfo visitInfo;
}
