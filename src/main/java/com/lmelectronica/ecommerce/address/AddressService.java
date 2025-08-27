package com.lmelectronica.ecommerce.address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmelectronica.ecommerce.shared.dtos.AddressDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateAddressRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    @Transactional
    public AddressDTO saveAddress(AddressDTO addressDTO, String username){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setNumber(addressDTO.getNumber());
        address.setCity(addressDTO.getCity());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setProvince(addressDTO.getProvince());
        address.setOtherInfo(addressDTO.getOtherInfo());
        address.setUser(user);
        
        Address saved = addressRepository.save(address);
        
        return mapAddressDTO(saved);
    }

    public AddressDTO getAddressByUser(String username){
        Address address = addressRepository.findByUserUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Address", username));
        
        return mapAddressDTO(address);
    }

    public void updateAddress(String username, UpdateAddressRequest request){
        Address address = addressRepository.findByUserUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Address", username));
        
        if(request.getStreet() != null) address.setStreet(request.getStreet());
        if(request.getNumber() != null) address.setNumber(request.getNumber());
        if(request.getCity() != null) address.setCity(request.getCity());
        if(request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if(request.getProvince() != null) address.setProvince(request.getProvince());
        if(request.getOtherInfo() != null) address.setOtherInfo(request.getOtherInfo());

        addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(String username){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Address address = addressRepository.findByUser(userFound)
            .orElseThrow(() -> new ResourceNotFoundException("Address", userFound));
        
        userFound.setAddress(null);

        addressRepository.delete(address);
    }

    public AddressDTO mapAddressDTO(Address address){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setNumber(address.getNumber());
        addressDTO.setCity(address.getCity());
        addressDTO.setPostalCode(address.getPostalCode());
        addressDTO.setProvince(address.getProvince());
        addressDTO.setOtherInfo(address.getOtherInfo());

        return addressDTO;
    }
}
