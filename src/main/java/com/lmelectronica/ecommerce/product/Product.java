package com.lmelectronica.ecommerce.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lmelectronica.ecommerce.category.Category;
import com.lmelectronica.ecommerce.favorite.Favorite;
import com.lmelectronica.ecommerce.review.Review;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="products")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Date createdAt;
    private Date updatedAt;
    private String description;
    
    @OneToMany(mappedBy="product", cascade=CascadeType.ALL)
    @JsonManagedReference("product-favorite")
    private List<Favorite> favorites = new ArrayList<>();


    @ManyToMany
    @JoinTable(
        name= "product_category",
        joinColumns= @JoinColumn(name="product_id"),
        inverseJoinColumns = @JoinColumn(name="category_id")
        )
    private List<Category> categories = new ArrayList<>();
    
    @OneToMany(mappedBy="product")
    @JsonBackReference("review-product")
    private List<Review> reviews = new ArrayList<>();

    public void reduceStock(int quantity){
        if(quantity > this.stock){
            throw BusinessRuleException.insufficentStock(this.name, quantity, this.stock);
        }
        this.stock -= quantity;
    }

    public void restartStock(int quantity){
        this.stock += quantity;
        if (this.stock < 0) {
            throw BusinessRuleException.insufficentStock(this.name, quantity, this.stock);
        }
    }
}
