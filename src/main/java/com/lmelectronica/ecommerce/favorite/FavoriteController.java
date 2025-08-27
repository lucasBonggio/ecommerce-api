package com.lmelectronica.ecommerce.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.FavoriteDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Favorite", description = "API for managing user favorites")
@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
        summary = "Create a favorite",
        description = "Adds a product to the authenticated user's favorites list."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Favorite created successfully",
            content = @Content(schema = @Schema(implementation = FavoriteDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid product ID or request data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or product not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Favorite already exists"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        )
    })
    @PostMapping("/create-favorite")
    public ResponseEntity<FavoriteDTO> createFavorite(
            @RequestBody FavoriteDTO favoriteDTO,
            Authentication authentication) {

        String username = authentication.getName();
        FavoriteDTO favorite = favoriteService.createFavorite(username, favoriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    @Operation(
        summary = "Get user's favorites with pagination",
        description = "Returns a paginated list of the authenticated user's favorite products. Requires valid JWT token."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Favorites retrieved successfully",
            content = @Content(
                schema = @Schema(implementation = Page.class))
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
    @GetMapping("/by-user")
    public ResponseEntity<Page<FavoriteDTO>> getFavoritesByUser(
            Authentication authentication,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc") String direction) {

        String username = authentication.getName();

        Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Page<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(username, page, size, sort);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
        summary = "Remove a favorite",
        description = "Removes a specific product from the authenticated user's favorites list."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Favorite removed successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Favorite not found"
        )
    })
    @DeleteMapping("/delete-favorite")
    public ResponseEntity<Void> removeFavorite(
            @RequestParam Long productId,
            Authentication authentication) {

        String username = authentication.getName();
        favoriteService.removeFavorite(username, productId);
        return ResponseEntity.noContent().build();
    }
}