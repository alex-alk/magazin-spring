package com.magazin.dao;

import com.magazin.model.Order;
import com.magazin.repository.OrderRepository;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderDAO {
    private final OrderRepository orderRepository;

    public OrderDAO(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAll(){
        return orderRepository.findAll();
    }
    public Order getOrderById(Long id){
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new EntityNotFoundException("Not found");
        }
    }
    public void addOrder (Order order) {
        orderRepository.save(order);
    }
    public void deleteOrder (Order order) {
        orderRepository.delete(order);
    }
}