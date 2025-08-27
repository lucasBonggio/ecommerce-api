package com.lmelectronica.ecommerce.category;

import org.springframework.data.domain.Page;
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

import com.lmelectronica.ecommerce.shared.dtos.CategoryDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateCategoryRequest;
import com.lmelectronica.ecommerce.shared.exceptions.EcommerceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name="Category", description="API that manages categories. ")
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService; 

    @Operation(summary="Create category",
                description="Create a new category. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="201",
                    description="Category created sucessfully. ",
                    content= @Content(schema= @Schema(implementation= CategoryDTO.class))),
        @ApiResponse(responseCode="409",
                    description="The category already exists. ",
                    content= @Content) 
    })
    @PostMapping("/create-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(
        @Parameter(description="Category data to be created. ", required= true)
        @RequestBody CategoryDTO categoryDTO){
        try {
            CategoryDTO category = categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @Operation(summary="Get category by ID",
                description="Retrieves a specific category based on its ID. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Category found successfully. ",
                    content=@Content(schema= @Schema(implementation= CategoryDTO.class))),
        @ApiResponse(responseCode="404",
                    description="Category not found. ",
                    content= @Content)
    })
    @GetMapping("/by-id")
    public ResponseEntity<CategoryDTO> getCategoryById(
        @Parameter(description="ID of the category to retrieve. ", required=true)
        @RequestParam Long id){
        try {
            CategoryDTO category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary="Get category by name",
                description="Retrieves a specific category based on its name. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Category found successfully. ",
                    content=@Content(schema= @Schema(implementation= CategoryDTO.class))),
        @ApiResponse(responseCode="404",
                    description="Category not found. ",
                    content= @Content)
    })
    @GetMapping("/by-name")
    public ResponseEntity<CategoryDTO> getCategoryByName(
        @Parameter(description="Name of the category to retrieve. ", required=true)
        @RequestParam String name){
        try {
            CategoryDTO category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(category);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(
        summary = "Get all categories with pagination",
        description = "Returns a paginated list of all categories. Supports sorting and filtering."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved a paginated list of categories",
            content = @Content(
                schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error. ",
            content= @Content
        )}) 
    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<CategoryDTO> categories = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update category",
                description = "Updates an existing category with new information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Category updated successfully.",
                    content = @Content),
        @ApiResponse(responseCode = "404",
                    description = "Category not found.",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-category")
    public ResponseEntity<Void> updateCategory(
            @Parameter(description = "Category update data", required = true)
            @RequestBody UpdateCategoryRequest request, 
            
            @Parameter(description = "ID of the category to update", required = true, example = "123")
            @RequestParam Long id) {
        
            categoryService.updateCategory(id, request);
            return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete category. ",
                description = "Deletes a category from the system. ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Category deleted successfully. ",
                    content = @Content),
        @ApiResponse(responseCode = "401",
                    description = "Unauthorized operation. ",
                    content = @Content)
    })
    @DeleteMapping("/delete-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to delete. ", required = true, example = "123")
            @RequestParam Long id) {
        
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
