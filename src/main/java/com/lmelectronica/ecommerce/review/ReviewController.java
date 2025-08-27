package com.lmelectronica.ecommerce.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.ReviewCreateDTO;
import com.lmelectronica.ecommerce.shared.dtos.ReviewDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateReviewRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
@Tag(name = "Review", description = "API for managing product reviews")
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
        summary = "Create a review",
        description = "Creates a new review for a product by the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Review created successfully",
            content = @Content(schema = @Schema(implementation = ReviewDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid review data or rating out of range"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or product not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already reviewed this product"
        )
    })
    @PostMapping("/create-review")
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody  ReviewCreateDTO request,
            @RequestParam Long productId,
            Authentication authentication) {

        String username = authentication.getName();
        ReviewDTO review = reviewService.createReview(productId, username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @Operation(
        summary = "Get review by ID",
        description = "Retrieves a specific review by its ID. Only accessible by ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Review retrieved successfully",
            content = @Content(schema = @Schema(implementation = ReviewDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: only ADMIN can view any review"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found"
        )
    })
    @GetMapping("/admin/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @Operation(
        summary = "Get user's reviews with pagination",
        description = "Returns a paginated list of all reviews created by the authenticated user. Requires a valid JWT token."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reviews retrieved successfully",
            content = @Content(
                schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content= @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content= @Content
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/by-user")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUser(
            Authentication authentication,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., id, rating, createdAt)")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String direction) {

        String username = authentication.getName();

        Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Page<ReviewDTO> reviews = reviewService.getReviewsByUser(username, page, size, sort);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
        summary = "Get reviews by product",
        description = "Retrieves all reviews associated with a specific product."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reviews retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found or has no reviews"
        )
    })
    @GetMapping("/by-product/{id}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
        @PathVariable Long id,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size,
        @RequestParam(defaultValue="id") String sortBy,
        @RequestParam(defaultValue="asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(id, page, size, sort);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
        summary = "Update a review",
        description = "Updates an existing review. Only the owner can update their own review."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Review updated successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data or rating out of range"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: cannot update another user's review"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found"
        )
    })
    @PutMapping("/update-review/{id}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        reviewService.updateReview(username, id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Delete a review",
        description = "Deletes a review. Only the owner can delete their own review."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Review deleted successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: cannot delete another user's review"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Review not found"
        )
    })
    @DeleteMapping("/delete-review/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        reviewService.deleteReview(username, id);
        return ResponseEntity.noContent().build();
    }
}