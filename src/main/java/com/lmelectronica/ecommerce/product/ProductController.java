package com.lmelectronica.ecommerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

import com.lmelectronica.ecommerce.shared.dtos.ProductDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateProductRequest;
import com.lmelectronica.ecommerce.shared.exceptions.EcommerceException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@Tag(name="Product", description="API that manages products. ")
@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary="Create product",
                description="Create a new product. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="201",
                    description="Product created successfully. ",
                    content= @Content(schema= @Schema(implementation= ProductDTO.class))),
        @ApiResponse(responseCode="409",
                    description="The product name already exists. ",
                    content= @Content)})
    @PostMapping("/create-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
        @Parameter(description="Product date to be created. ", required=true)
        @RequestBody ProductDTO productDTO){
        try {
            ProductDTO product = productService.createProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @Operation(summary="Get product by id. ",
                description="Retrieve a product by its ID. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Product retrieve successfully. ",
                    content= @Content(schema= @Schema(implementation= ProductDTO.class))),
        @ApiResponse(responseCode="404",
                    description="Product not found. ",
                    content= @Content)})
    @GetMapping("/by-id")
    public ResponseEntity<ProductDTO> getProductById(
        @Parameter(description="ID of the product. ")
        @RequestParam Long id){
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (EcommerceException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary="Get product by name. ",
                description="Retrieve a product by its name. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Product retrieve successfully. ",
                    content= @Content(schema= @Schema(implementation= ProductDTO.class))),
        @ApiResponse(responseCode="404",
                    description="Product not found. ",
                    content= @Content)})
    @GetMapping("/by-name")
    public ResponseEntity<ProductDTO> getProductByName(
        @Parameter(description="Name of the product. ")
        @RequestParam String name){
        try {
            ProductDTO product = productService.getProductByName(name);
            return ResponseEntity.ok(product);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(
        summary = "Get all products with pagination and sorting",
        description = "Returns a paginated and sortable list of all available products. Supports dynamic sorting by any field."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved a paginated list of products",
            content = @Content(
                schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., id, name, price)")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = "desc".equalsIgnoreCase(direction)
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Page<ProductDTO> products = productService.getAllProducts(page, size, sort);
        return ResponseEntity.ok(products);
    }

    @Operation(summary="Update product",
                description="Updates an existing product with new information. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Product updated successfully. ",
                    content= @Content),
        @ApiResponse(responseCode="404",
                    description="Product not found. ",
                    content= @Content)})
    @PutMapping("/update-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateProduct(
        @Parameter(description="Product update data. ", required=true)
        @RequestBody UpdateProductRequest request, 

        @Parameter(description="ID of the product to update. ", required=true)
        @RequestParam Long id){
        try {
            productService.updateProduct(id, request);
            return ResponseEntity.ok().build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary="Delete product. ",
                description="Deletes a product from the system. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="204",
                    description="Product updated successfully. ",
                    content= @Content),
        @ApiResponse(responseCode="500",
                    description="Internal server error. ",
                    content= @Content)})
    @DeleteMapping("/delete-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description="ID of the product to delete. ")
        @RequestParam Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
