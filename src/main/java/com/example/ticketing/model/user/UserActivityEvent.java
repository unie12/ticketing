package com.example.ticketing.model.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SearchActivityEvent.class, name = "search"),
        @JsonSubTypes.Type(value = StoreViewActivityEvent.class, name = "store_view"),
        @JsonSubTypes.Type(value = ReviewActivityEvent.class, name = "review")
})
public abstract class UserActivityEvent {
    private UserActivity eventType;
    private Long userId;
    private String storeId;
    private LocalDateTime timestamp;
    //    private Long reviewId;
//    private Map<String, Object> metadata;

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
