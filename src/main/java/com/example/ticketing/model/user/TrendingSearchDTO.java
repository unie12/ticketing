package com.example.ticketing.model.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class TrendingSearchDTO {
    private String query;
    private Long searchCount;
    private LocalDateTime lastSearched;
//    private List<String> relatedCategories;

    public static List<TrendingSearchDTO> from(Set<ZSetOperations.TypedTuple<String>> results) {
        if (results == null) {
            return new ArrayList<>();
        }

        return results.stream()
                .map(tuple -> TrendingSearchDTO.builder()
                        .query(tuple.getValue())
                        .searchCount(tuple.getScore().longValue())
                        .lastSearched(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }
}
