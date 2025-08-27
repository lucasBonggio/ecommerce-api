package com.lmelectronica.ecommerce.favorite;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.user.User;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="favorites")
public class Favorite {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference("user-favorites")
    private User user;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonBackReference("product-favorite")
    private Product product;
}
