package com.lmelectronica.ecommerce.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lmelectronica.ecommerce.order.Order;
import com.lmelectronica.ecommerce.order.OrderRepository;
import com.lmelectronica.ecommerce.orderItem.OrderItem;
import com.lmelectronica.ecommerce.orderItem.OrderItemRepository;
import com.lmelectronica.ecommerce.orderItem.OrderItemService;
import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.OrderItemDTO;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrderItemTests {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    private Product product;
    private Order order;
    private OrderItem orderItem;

    
    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setStock(10);
        product.setPrice(100.0);

        order = new Order();
        order.setId(1L);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(200.0);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
    }

    @Test
    void createItem_validData_returnDTO(){
        OrderItemDTO item = new OrderItemDTO();
        item.setQuantity(2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invoctacion -> invoctacion.getArgument(0));

        OrderItemDTO result = orderItemService.createOrderItem(item, 1L, 1L);
    
        assertNotNull(result);
        assertEquals(2, result.getQuantity());

        verify(productRepository).findById(1L);
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void createItem_productNotFound_throwException(){
        OrderItemDTO item = new OrderItemDTO();
        item.setQuantity(2);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderItemService.createOrderItem(item, 1L, 2L));
        
        String messageExpected = String.format("Product with id '%s' not found. ", 1L);

        assertEquals(ex.getMessage(), messageExpected);
        verify(productRepository).findById(1L);
    }

    @Test
    void createItem_orderNotFound_throwException(){
        OrderItemDTO item = new OrderItemDTO();
        item.setQuantity(1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderItemService.createOrderItem(item, 1L, 1L));
        
        String messageExpected = String.format("Order with id '%s' not found. ", 1L);

        assertEquals(ex.getMessage(), messageExpected);
        verify(productRepository).findById(1L);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getItemsByOrder_validData_returnList(){
        Long orderId = 1L;
        List<OrderItem> mockItems = List.of(orderItem);

        
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(mockItems);

        List<OrderItemDTO> result = orderItemService.getItemsByOrder(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void getItemsByOrder_returnEmptyList(){
        Long orderId = 1L;

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());
        List<OrderItemDTO> result = orderItemService.getItemsByOrder(orderId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderItemRepository).findByOrderId(orderId);
    }

    @Test
    void getItemById_validData_returnDTO(){
        Long itemId = 1L;

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(orderItem));

        OrderItemDTO itemDTO = orderItemService.getItemById(itemId);

        assertNotNull(itemDTO);
        assertEquals(2, itemDTO.getQuantity());

        verify(orderItemRepository).findById(itemId);
    }

    @Test
    void getItemById_itemNotFound_throwException(){
        Long itemId = 1L;

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderItemService.getItemById(itemId));

        String messageExpected = String.format("OrderItem with id '%s' not found. ", itemId);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderItemRepository).findById(itemId);
    }

    @Test
    void updateOrderItem_validChanges(){
        Long itemId = 1L;

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setQuantity(3);

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderItemService.updateOrderItem(itemId, itemDTO);

        assertEquals(3, orderItem.getQuantity());
        assertEquals(300.0, orderItem.getPrice());
        
        verify(orderItemRepository).findById(itemId);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void updateOrderItem_itemNotFound_returnException(){
        Long itemId = 1L;

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setQuantity(4);

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderItemService.updateOrderItem(itemId, itemDTO));

        String messageExpected = String.format("OrderItem with id '%s' not found. ", itemId);

        assertEquals(ex.getMessage(), messageExpected);
    }


    @Test 
    void deleteItem_validDelete(){
        Long itemId = 1L;

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(orderItem));
        
        orderItemService.deleteOrderItem(itemId);

        verify(orderItemRepository).findById(itemId);
        verify(orderItemRepository).delete(orderItem);
    }

    @Test 
    void deleteItem_itemNotFound(){
        Long itemId = 1L;

        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderItemService.deleteOrderItem(itemId));

        String messageExpected = String.format("OrderItem with id '%s' not found. ", itemId);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderItemRepository).findById(itemId);
    }
}
