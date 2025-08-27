package com.lmelectronica.ecommerce.shared.dtos;

import java.sql.Date;

import com.lmelectronica.ecommerce.order.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String billingAddress;
    private Date createdAt;
    private Status status;
    private Long userId;
    private double totalAmount;
}
