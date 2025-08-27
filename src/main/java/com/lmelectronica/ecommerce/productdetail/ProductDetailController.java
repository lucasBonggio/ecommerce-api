package com.lmelectronica.ecommerce.productdetail;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.ProductDetailDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateDetailRequest;
import com.lmelectronica.ecommerce.shared.exceptions.EcommerceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name="Product detail", description="API that manages product details. ")
@RestController
@RequestMapping("/product-detail")
@AllArgsConstructor
public class ProductDetailController {
    private final ProductDetailService productDetailService;

    @Operation(summary="Created product detail. ",
                description="Created a new product detail. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="201",
                    description="Detail created successfully. ",
                    content= @Content(schema= @Schema(implementation= ProductDetailDTO.class))),
        @ApiResponse(responseCode="409",
                    description="The product you are trying to link to was not found. ",
                    content= @Content)})
    @PostMapping("/create-detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailDTO> createDetail(
        @Parameter(description="Detail data to be created. ", required=true)
        @RequestBody ProductDetailDTO productDetailDTO, 
        
        @Parameter(description="The ID of the product to which it will be related. ", required=true)
        @RequestParam Long id){
        try {
            ProductDetailDTO detail = productDetailService.createDetail(productDetailDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(detail);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @Operation(summary="Get details by product id. ",
                description="Retrieves all the details of a product based on its ID. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Details retrieved successfully. ",
                    content= @Content(schema= @Schema(implementation= ProductDetailDTO.class))),
        @ApiResponse(responseCode="500",
                    description="Internal server error. ",
                    content= @Content)})
    @GetMapping("/by-product")
    public ResponseEntity<List<ProductDetailDTO>> getDetailsByProductId(
        @Parameter(description="Product ID of the owner of the details. ", required=true)
        @RequestParam Long id){
        List<ProductDetailDTO> details = productDetailService.getDetailByProductId(id);
        
        return ResponseEntity.ok(details); 
    }

    @Operation(summary="Update detail. ", 
                description="Updates an existing detail with new information. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Detail updated successfully. ",
                    content= @Content),
        @ApiResponse(responseCode="404", 
                    description="Detail not found. ",
                    content= @Content)})
    @PutMapping("/update-detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateDetail(
        @Parameter(description="Detail update data. ", required=true)
        @RequestBody UpdateDetailRequest request, 
        
        @Parameter(description="ID of the detail to update. ", required=true)
        @RequestParam Long id){
        try {
            productDetailService.updateDetail(id, request);
            return ResponseEntity.ok().build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary="Delete detail. ",
                description="Deletes a detail from the system. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="204",
                    description="Detail deleted successfully. ",
                    content= @Content),
        @ApiResponse(responseCode="409",
                    description="Unauthorized operation. ",
                    content= @Content)})
    @DeleteMapping("/delete-detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDetail(
        @Parameter(description="ID of the detail to delete. ", required=true)
        @RequestParam Long id){
        try {
            productDetailService.deleteDetail(id);
            return ResponseEntity.noContent().build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
