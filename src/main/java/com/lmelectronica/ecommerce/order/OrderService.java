package com.lmelectronica.ecommerce.order;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmelectronica.ecommerce.shared.dtos.OrderDTO;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, String username){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Order order = new Order();
        order.setBillingAddress(orderDTO.getBillingAddress());
        order.setUser(user);
        order.setStatus(Status.pending);
        order.setCreatedAt(new Date());

        Order orderSaved = orderRepository.save(order);

        return mapOrderDTO(orderSaved);
    }

    public Page<OrderDTO> getAllOrders(int page, int size, Sort sort){
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findAll(pageable);
        
        return orders.map(order -> mapOrderDTO(order));
    }

    public Page<OrderDTO> getOrdersByUser(String username, int page, int size, Sort sort){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findByUserId(userFound.getId(), pageable);

        return orders.map(order -> mapOrderDTO(order));
    }
    
    public OrderDTO getOrderById(Long id){
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        return mapOrderDTO(order);
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO, Long orderId, String username){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        if(!order.getUser().getId().equals(user.getId())) throw new BusinessRuleException("You can only update your own orders. ");
            
        if(orderDTO.getBillingAddress() != null) order.setBillingAddress(orderDTO.getBillingAddress());
        if(orderDTO.getStatus() != null) order.setStatus(orderDTO.getStatus());

        Order orderSaved = orderRepository.save(order);

        return mapOrderDTO(orderSaved);
    }

    @Transactional
    public void deleteOrder(String username, Long orderId){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if(!order.getUser().getId().equals(userFound.getId())){
            throw new BusinessRuleException("You can only delete your own orders. ");
        }

        orderRepository.delete(order);
    }

    public double calculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        order.setTotalAmount(total);
        orderRepository.save(order);

        return total;
    }

    public OrderDTO mapOrderDTO(Order order){
        OrderDTO orderMap = new OrderDTO();
        orderMap.setId(order.getId());
        orderMap.setBillingAddress(order.getBillingAddress());
        orderMap.setStatus(order.getStatus());
        orderMap.setUserId(order.getUser().getId());
        orderMap.setTotalAmount(order.getTotalAmount());

        return orderMap;
    }
}
