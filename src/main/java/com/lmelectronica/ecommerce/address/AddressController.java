package com.lmelectronica.ecommerce.address;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.AddressDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateAddressRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@Tag(name = "Address", description = "API that manages addresses.")
@RestController
@RequestMapping("/address")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Create address")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Address created"),
        @ApiResponse(responseCode = "409", description = "User not found")
    })
    @PostMapping("/create-address")
    public ResponseEntity<AddressDTO> createAddress(
            @RequestBody AddressDTO addressDTO,
            Authentication authentication) {

        String username = authentication.getName();
        AddressDTO address = addressService.saveAddress(addressDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @Operation(summary = "Get user's address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address found"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/by-user")
    public ResponseEntity<AddressDTO> getAddressByUser(Authentication authentication) {
        String username = authentication.getName();
        AddressDTO address = addressService.getAddressByUser(username);
        return ResponseEntity.ok(address);
    }

    @Operation(summary = "Update user's address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Address updated"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/update-address")
    public ResponseEntity<Void> updateAddress(
            @RequestBody UpdateAddressRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        addressService.updateAddress(username, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete user's address")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Address deleted"),
        @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/delete-address")
    public ResponseEntity<Void> deleteAddress(Authentication authentication) {
        String username = authentication.getName();
        addressService.deleteAddress(username);
        return ResponseEntity.noContent().build();
    }
}