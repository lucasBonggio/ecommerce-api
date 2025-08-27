package com.lmelectronica.ecommerce.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lmelectronica.ecommerce.user.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByUser(User user, Pageable pageable);
}
