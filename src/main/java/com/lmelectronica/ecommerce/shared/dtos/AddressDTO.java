    package com.lmelectronica.ecommerce.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String province;
    private String otherInfo;
}
