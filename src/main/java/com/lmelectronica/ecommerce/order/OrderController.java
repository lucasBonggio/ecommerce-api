package com.lmelectronica.ecommerce.order;

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

import com.lmelectronica.ecommerce.shared.dtos.OrderDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Order", description = "API for managing orders")
@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid order data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @PostMapping("/create-order")
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderDTO orderDTO,
            Authentication authentication) {

        String username = authentication.getName();
        OrderDTO order = orderService.createOrder(orderDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(
        summary = "Get all orders",
        description = "Retrieves a complete list of all orders. Only accessible by ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: only ADMIN can view all orders"
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size,
        @RequestParam(defaultValue="id") String sortBy,
        @RequestParam(defaultValue="asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

        Page<OrderDTO> orders = orderService.getAllOrders(page, size, sort);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get user's orders",
        description = "Retrieves a list of all orders placed by the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        )
    })
    @GetMapping("/by-user")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUser(
        Authentication authentication,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size,
        @RequestParam(defaultValue="id") String sortBy,
        @RequestParam(defaultValue="asc") String direction) {

        String username = authentication.getName();
        Sort sort = direction.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

        Page<OrderDTO> orders = orderService.getOrdersByUser(username, page, size, sort);
        
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its ID. Only accessible by ADMIN."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Order retrieved successfully",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: only ADMIN can view any order"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found"
        )
    })
    @GetMapping("/admin/order/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(
        summary = "Update an order",
        description = "Updates an existing order. Only the owner can update their own order."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Order updated successfully",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data or update not allowed"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: cannot update another user's order"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found"
        )
    })
    @PutMapping("/update-order/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderDTO orderDTO,
            Authentication authentication) {

        String username = authentication.getName();
        OrderDTO order = orderService.updateOrder(orderDTO, id, username);
        return ResponseEntity.ok(order);
    }

    @Operation(
        summary = "Delete an order",
        description = "Deletes an order. Only the owner can delete their own order, and only if it's pending."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Order deleted successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied: cannot delete another user's order or non-pending order"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found"
        )
    })
    @DeleteMapping("/delete-order/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        orderService.deleteOrder(username, id);
        return ResponseEntity.noContent().build();
    }
}