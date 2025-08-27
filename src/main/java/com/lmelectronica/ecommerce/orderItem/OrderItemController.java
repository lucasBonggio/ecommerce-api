package com.lmelectronica.ecommerce.orderItem;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lmelectronica.ecommerce.shared.dtos.OrderItemDTO;
import com.lmelectronica.ecommerce.shared.exceptions.EcommerceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name="Order item", description="API that manages order items. ")
@RestController
@RequestMapping("/order-item")
@AllArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(summary="Create order item. ",
                description="Create a new item for an order. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="201",
                    description="Item created successfully. ",
                    content=@Content(schema=@Schema(implementation= OrderItemDTO.class))),
        @ApiResponse(responseCode="409",
                    description="The product or order to be linked was not found. ",
                    content= @Content)})
    @PostMapping("/create-item")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderItemDTO> createItem(
        @Parameter(description="Item data to be created. ", required=true)
        @RequestBody OrderItemDTO orderItemDTO, 

        @Parameter(description="Related product ID. ", required=true)
        @RequestParam Long productId, 

        @Parameter(description="Related order ID. ", required=true)
        @RequestParam Long orderId){
        try {
            OrderItemDTO item = orderItemService.createOrderItem(orderItemDTO, productId, orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @Operation(summary="Get items by order. ",
                description="Retrieves all items from a specific order. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Retrieves a complete list of all available items of the specific ID. ",
                    content= @Content(schema= @Schema(implementation= OrderItemDTO.class))),
        @ApiResponse(responseCode="500",
                    description="Internal server error. ",
                    content= @Content)})
    @GetMapping("/by-order")
    public ResponseEntity<List<OrderItemDTO>> getItemsByOrders(
        @Parameter(description="ID of the order where the items belong. ", required=true)
        @RequestParam Long id){
        List<OrderItemDTO> items = orderItemService.getItemsByOrder(id);

        return ResponseEntity.ok(items);
    }

    @Operation(summary="Get items by id. ",
                description="Retrieves an item from a specific ID. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Recover the specific item. ",
                    content= @Content(schema= @Schema(implementation= OrderItemDTO.class))),
        @ApiResponse(responseCode="494",
                    description="Item not found. ",
                    content= @Content)})
    @GetMapping("/by-id")
    public ResponseEntity<OrderItemDTO> getItemById(@RequestParam Long id){
        try {
            OrderItemDTO item = orderItemService.getItemById(id);
            return ResponseEntity.ok(item);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary="Update item. ",
                description="Updates an existing item with new information. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="200",
                    description="Item updated successfully. ",
                    content= @Content(schema= @Schema(implementation= OrderItemDTO.class))),
        @ApiResponse(responseCode="404",
                    description="Item not found. ",
                    content= @Content)})
    @PutMapping("/update-item")
    public ResponseEntity<OrderItemDTO> updateItem(
        @Parameter(description="Item update data. ", required=true)    
        @RequestBody OrderItemDTO orderItemDTO, 

        @Parameter(description="ID of the item to update. ", required=true)
        @RequestParam Long id){
        try {
            OrderItemDTO item = orderItemService.updateOrderItem(id, orderItemDTO);
            return ResponseEntity.ok(item);
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary="Delete item. ", 
                description="Deletes a item from the system. ")
    @ApiResponses(value={
        @ApiResponse(responseCode="204",
                    description="Item deleted successfully. ",
                    content= @Content),
        @ApiResponse(responseCode="401",
                    description="Unauthorized operation. ",
                    content= @Content)})
    @DeleteMapping("/delete-item")
    public ResponseEntity<Void> deleteItem(
        @Parameter(description="ID of the item to delete. ", required=true)
        @RequestParam Long id){
        try {
            orderItemService.deleteOrderItem(id);
            return ResponseEntity.noContent().build();
        } catch (EcommerceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
