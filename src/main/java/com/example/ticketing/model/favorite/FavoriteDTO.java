package com.example.ticketing.model.favorite;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FavoriteDTO {
    private Long id;
    private String username;
    private String placeName;
    private LocalDateTime createdAt;

    public static FavoriteDTO from(Favorite favorite) {
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .username(favorite.getUser().getUsername())
                .placeName(favorite.getStore().getPlaceName())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
