package com.lmelectronica.ecommerce.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lmelectronica.ecommerce.orderItem.OrderItem;
import com.lmelectronica.ecommerce.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String billingAddress;
    private Date createdAt;
    private Status status;
    private double totalAmount;


    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonManagedReference("order-user")
    private User user;

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
    @JsonBackReference("orderItem-order")
    private List<OrderItem> items = new ArrayList<>();
}
