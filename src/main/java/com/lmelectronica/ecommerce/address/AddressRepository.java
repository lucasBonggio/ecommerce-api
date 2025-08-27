package com.lmelectronica.ecommerce.address;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lmelectronica.ecommerce.user.User;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUserUsername(String username);

    Optional<Address> findByUser(User user);
}
