package com.lmelectronica.ecommerce.favorite;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lmelectronica.ecommerce.user.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.product.id = :productId")
    Optional<Favorite> findByUserIdAndProductId(@Param("userId") Long userId, 
                                                @Param("productId") Long productId);

    List<Favorite> findFavoritesByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    long countByProductId(Long productId);

    Page<Favorite> findFavoritesByUser(User user, Pageable pageable);

}
