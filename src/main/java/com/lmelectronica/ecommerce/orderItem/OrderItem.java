package com.lmelectronica.ecommerce.orderItem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lmelectronica.ecommerce.order.Order;
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
@Table(name="orders_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double price;
    
    @ManyToOne
    @JoinColumn(name="order_id")
    @JsonBackReference("orderItem-order")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonManagedReference("orderItem-product")
    private Product product;
}
