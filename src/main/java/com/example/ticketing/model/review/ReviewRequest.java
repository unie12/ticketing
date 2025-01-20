package com.example.ticketing.model.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.apache.catalina.LifecycleState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewRequest {
    private String content;
    private Integer rating;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime visitDateTime;
    private Crowdedness crowdedness;
//    private List<String> imageUrls;
}
