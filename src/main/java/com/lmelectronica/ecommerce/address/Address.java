package com.lmelectronica.ecommerce.address;

import com.lmelectronica.ecommerce.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="address")
public class Address {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String province;
    private String otherInfo;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
