package com.lmelectronica.ecommerce.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lmelectronica.ecommerce.address.Address;
import com.lmelectronica.ecommerce.address.AddressRepository;
import com.lmelectronica.ecommerce.address.AddressService;
import com.lmelectronica.ecommerce.shared.dtos.AddressDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateAddressRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTests {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Test
    void createAddress_validData_returnAddressDTO(){
        Address address = new Address();
        address.setCity("Almagro");
        address.setNumber("162");
        address.setProvince("CABA");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity("Almagro");
        addressDTO.setNumber("162");
        addressDTO.setProvince("CABA");
        
        User user = new User();
        user.setUsername("username");
        user.setId(1L);
        user.setFirstName("Lucas");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        
        AddressDTO result = addressService.saveAddress(addressDTO, user.getUsername());

        assertNotNull(result);
        assertEquals(result.getCity(), address.getCity());
        assertEquals(result.getNumber(), address.getNumber());
        assertEquals(result.getProvince(), address.getProvince());
        
        verify(userRepository).findByUsername("username");
        verify(addressRepository).save(any(Address.class));   
    }

    @Test
    void craeteAddress_userNotFound_throwException(){
        AddressDTO addressDTO = new AddressDTO();
        String username = "usernameRandom";

        when(userRepository.findByUsername("usernameRandom")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> addressService.saveAddress(addressDTO, username));

        String messageExpected = String.format("User with id '%s' not found. ", username);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getAddressByUser_validData_returnAddressDTO(){
        Address address = new Address();
        address.setCity("Almagro");
        address.setNumber("162");
        address.setProvince("CABA");

        when(addressRepository.findByUserUsername("username")).thenReturn(Optional.of(address));

        AddressDTO result = addressService.getAddressByUser("username");

        assertNotNull(result);
        assertEquals(address.getCity(), result.getCity());
        assertEquals(address.getNumber(), result.getNumber());
        assertEquals(address.getProvince(), result.getProvince());
    }

    @Test
    void updateAddress_validData(){
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Liniers");
        request.setProvince("CABA");
        
        Address addressExisting = new Address();
        addressExisting.setCity("Cuidadela");
        addressExisting.setProvince("Buenos Aires");

        when(addressRepository.findByUserUsername("username")).thenReturn(Optional.of(addressExisting));

        addressService.updateAddress("username", request);

        assertEquals("Liniers", addressExisting.getCity());
        assertEquals("CABA", addressExisting.getProvince());
    }

    @Test
    void updateAddress_addresNotFound_returnException(){
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Liniers");
        request.setProvince("CABA");

        when(addressRepository.findByUserUsername("username")).thenReturn(Optional.empty());


        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> addressService.updateAddress("username", request));

        String messageExpected = String.format("Address with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void deleteAddress_verifyDelete(){
        Address addressExisting = new Address();
        addressExisting.setCity("Cuidadela");
        addressExisting.setProvince("Buenos Aires");

        User user = new User();
        user.setUsername("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(Optional.of(addressExisting));

        addressService.deleteAddress("username");
        
        verify(addressRepository).delete(addressExisting);
    }

    @Test
    void deleteAddress_addressNotFound_returnException(){
        User user = new User();
        user.setUsername("username");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> addressService.deleteAddress("username"));

        String messageExpected = String.format("Address with id '%s' not found. ", user);

        assertEquals(ex.getMessage(), messageExpected);
    }
}
