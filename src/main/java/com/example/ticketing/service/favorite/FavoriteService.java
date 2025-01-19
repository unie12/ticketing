package com.example.ticketing.service.favorite;

import com.example.ticketing.model.favorite.FavoriteDTO;

import java.util.List;

public interface FavoriteService {
    FavoriteDTO toggleFavorite(String storeId, Long userId);
    List<FavoriteDTO> getMyFavorites(Long userId);
}
