package com.example.ticketing.service.favorite;

import com.example.ticketing.model.favorite.Favorite;
import com.example.ticketing.model.favorite.FavoriteDTO;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.favorite.FavoriteRepository;
import com.example.ticketing.service.store.StoreService;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService{
    private final UserService userService;
    private final StoreService storeService;
    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public FavoriteDTO toggleFavorite(String storeId, Long userId) {
        User user = userService.findUserById(userId);
        Store store = storeService.findStoreById(storeId);

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndStore(user, store);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            store.decrementFavoriteCount();
//            return FavoriteDTO.from(existingFavorite.get());
            return null;
        } else {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .store(store)
                    .build();
            FavoriteDTO dto = FavoriteDTO.from(favoriteRepository.save(favorite), true);
            store.incrementFavoriteCount();
            return dto;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getMyFavorites(Long userId) {
        User user = userService.findUserById(userId);
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(favorite -> {
                    return FavoriteDTO.from(favorite, favoriteRepository.existsByUserIdAndStoreId(userId, favorite.getStore().getId()));
                })
                .collect(Collectors.toList());
    }
}
