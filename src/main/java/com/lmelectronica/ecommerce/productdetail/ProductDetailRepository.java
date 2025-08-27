package com.lmelectronica.ecommerce.productdetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends  JpaRepository<ProductDetail, Long>{
    List<ProductDetail> findByProductId(Long productId);
}
