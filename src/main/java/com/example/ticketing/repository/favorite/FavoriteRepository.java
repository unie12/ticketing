package com.example.ticketing.repository.favorite;

import com.example.ticketing.model.favorite.Favorite;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndStore(User user, Store store);

    List<Favorite> findByUser(User user);

    @Query("SELECT f.store.id FROM Favorite f WHERE f.user.id = :userId")
    Set<String> findStoreIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.store.id = :storeId")
    boolean existsByUserIdAndStoreId(@Param("userId") Long userId, @Param("storeId") String storeId);
}
