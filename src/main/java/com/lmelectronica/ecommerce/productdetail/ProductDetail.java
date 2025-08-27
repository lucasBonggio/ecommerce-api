package com.lmelectronica.ecommerce.productdetail;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lmelectronica.ecommerce.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="products_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String details;
    private String keyName;
    
    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    @JsonManagedReference("product-detail")
    private Product product;
}
