package com.lmelectronica.ecommerce.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lmelectronica.ecommerce.order.Order;
import com.lmelectronica.ecommerce.order.OrderRepository;
import com.lmelectronica.ecommerce.order.OrderService;
import com.lmelectronica.ecommerce.order.Status;
import com.lmelectronica.ecommerce.orderItem.OrderItem;
import com.lmelectronica.ecommerce.shared.dtos.OrderDTO;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Order order;

    @Test
    void createOrder_validData_returnDTO(){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress("Debit card");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        OrderDTO result = orderService.createOrder(orderDTO, "username");

        assertNotNull(result);
        assertEquals("Debit card", result.getBillingAddress());
        assertEquals(Status.pending, result.getStatus());

        verify(orderRepository).save(any(Order.class));        
    }

    @Test
    void createOrder_userNotFound_returnException(){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress("Credit card");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderDTO, "username"));

        String messageExpected = String.format("User with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
        verify(userRepository).findByUsername("username");
    }


    @Test
    void getAllOrders_validData_returnPage(){
        List<Order> orders = List.of(order);
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        Page<OrderDTO> orderDTOs = orderService.getAllOrders(0, 10, sort);

        assertNotNull(orderDTOs);

        assertThat(orderDTOs.getContent())
                    .hasSize(1)
                    .extracting("billingAddress")
                    .contains("Debit Card");
    }

    @Test
    void getAllOrders_noOrders_returnEmptyPage(){
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Order> orderPage = Page.empty(pageable);


        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        Page<OrderDTO> orderDTOs = orderService.getAllOrders(0, 10, sort);

        assertNotNull(orderDTOs);
        assertThat(orderDTOs.getContent()).hasSize(0);

        verify(orderRepository).findAll(pageable);
    }

    @Test
    void getOrdersByUser_validData_returnPage(){
        List<Order> orders = List.of(order);
        Long userId = 1L;
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());


        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(userId, pageable)).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrdersByUser("username", 0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent())
                    .hasSize(1)
                    .extracting("id")
                    .contains(1L);
    } 

    @Test
    void getOrdersByUser_noOrders_returnEmptyPage(){
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Order> orderPage = Page.empty(pageable);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(user.getId(), pageable)).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrdersByUser("username", 0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent()).hasSize(0);
        verify(orderRepository).findByUserId(user.getId(), pageable);
    }

    @Test
    void getOrdersById_validData_returnDTO(){
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);

        assertEquals("Debit Card", result.getBillingAddress());
        
        verify(orderRepository).findById(orderId);    
    }

    @Test
    void getOrderById_orderNotFound_returnException(){
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(orderId));

        String messageExpected = String.format("Order with id '%s' not found. ", orderId);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void updateOrder_validChanges_returnDTO(){
        Long orderId = 1L;

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress("Credit card");
        orderDTO.setStatus(Status.cancelled);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.updateOrder(orderDTO, orderId, "username");

        assertNotNull(result);

        assertEquals("Credit card", result.getBillingAddress());
        assertEquals(Status.cancelled, result.getStatus());

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_orderNotFound_returnException(){
        Long orderId = 1L;

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress("Fake Street 123");
        orderDTO.setStatus(Status.cancelled);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(orderDTO, orderId, "username"));

        String messageExpected = String.format("Order with id '%s' not found. ", orderId);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void deleteOrder_validDelete(){
        Long orderId = 1L;

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder("username", orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_orderNotFound_returnException(){
        Long orderId = 1L;

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder("username", 1L));

        String messageExpected = String.format("Order with id '%s' not found. ", 1L);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void calculateOrderTotal_validOrderId_calculatesAndUpdatesTotal() {
        OrderItem item1 = new OrderItem();
        item1.setPrice(100.0);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPrice(50.0);
        item2.setQuantity(3);

        order.setItems(Arrays.asList(item1, item2)); 

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double result = orderService.calculateOrderTotal(1L);

        assertEquals(350.0, result);
        assertEquals(350.0, order.getTotalAmount()); 

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void calculateOrderTotal_invalidOrderId_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.calculateOrderTotal(1L));

        String messageExpected = String.format("Order with id '%s' not found. ", 1L);

        assertEquals(ex.getMessage(), messageExpected);
        verify(orderRepository).findById(1L);
    }
}
