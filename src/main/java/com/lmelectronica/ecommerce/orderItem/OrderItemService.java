package com.lmelectronica.ecommerce.orderItem;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmelectronica.ecommerce.order.Order;
import com.lmelectronica.ecommerce.order.OrderRepository;
import com.lmelectronica.ecommerce.order.OrderService;
import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.OrderItemDTO;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;
    
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    @Transactional
    public OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO, Long productId, Long orderId){
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
            
        product.reduceStock(orderItemDTO.getQuantity());
        
        OrderItem item = new OrderItem();
        item.setQuantity(orderItemDTO.getQuantity());
        item.setPrice(product.getPrice());
        item.setOrder(order);
        item.setProduct(product);

        orderService.calculateOrderTotal(orderId);

        OrderItem itemSaved = orderItemRepository.save(item);

        return mapOrderItemDTO(itemSaved);
    }

    public List<OrderItemDTO> getItemsByOrder(Long orderId){
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return items.stream()
                        .map(this::mapOrderItemDTO)
                        .collect(Collectors.toList());            
    }

    public OrderItemDTO getItemById(Long itemId){
        OrderItem item = orderItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem", itemId));

        return mapOrderItemDTO(item);
    }

    @Transactional
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO){
        OrderItem itemExisting = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem", id));
        
        Product product = itemExisting.getProduct();

        int quantityDifference = orderItemDTO.getQuantity() - itemExisting.getQuantity();

        if(quantityDifference > 0 && product.getStock() < quantityDifference){
            throw BusinessRuleException.insufficentStock(product.getName(), orderItemDTO.getQuantity(), product.getStock());
        }

        if(quantityDifference != 0){
            product.reduceStock(quantityDifference);
        }

        itemExisting.setQuantity(orderItemDTO.getQuantity());
        itemExisting.setPrice(orderItemDTO.getQuantity() * product.getPrice());

        OrderItem updatedItem = orderItemRepository.save(itemExisting);

        return mapOrderItemDTO(updatedItem);
    }

    @Transactional
    public void deleteOrderItem(Long id){
        OrderItem item = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem", id));
        
        Product product = item.getProduct();

        product.restartStock(item.getQuantity());
    
        orderItemRepository.delete(item);
    }

    public OrderItemDTO mapOrderItemDTO(OrderItem item){
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setQuantity(item.getQuantity());

        return orderItemDTO;
    }
}
