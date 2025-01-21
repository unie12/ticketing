package com.example.ticketing.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityEvent {
    private UserActivity eventType;
    private Long userId;
    private String storeId;
    private Long reviewId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

//    @JsonCreator
//    public UserActivityEvent(
//            @JsonProperty("eventType") UserActivity eventType,
//            @JsonProperty("userId") Long userId,
//            @JsonProperty("storeId") String storeId,
//            @JsonProperty("reviewId") Long reviewId,
//            @JsonProperty("timestamp") LocalDateTime timestamp,
//            @JsonProperty("metadata") Map<String, Object> metadata) {
//        this.eventType = eventType;
//        this.userId = userId;
//        this.storeId = storeId;
//        this.reviewId = reviewId;
//        this.timestamp = timestamp;
//        this.metadata = metadata;
//    }
}
