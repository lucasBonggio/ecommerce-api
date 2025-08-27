package com.lmelectronica.ecommerce.shared.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private Long productId;
    private Date createdAt;
}
