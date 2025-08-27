package com.lmelectronica.ecommerce.shared.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private Double price;
    private int stock;
    private Date createdAt = new Date();
    private String description;
}
