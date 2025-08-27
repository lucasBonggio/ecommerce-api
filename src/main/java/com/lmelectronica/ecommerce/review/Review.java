package com.lmelectronica.ecommerce.review;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name="reviews")
public class Review {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Date createdAt;
    private double rating;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonManagedReference("product-reviews")
    private Product product;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonManagedReference("user-reviews")
    private User user;
}
