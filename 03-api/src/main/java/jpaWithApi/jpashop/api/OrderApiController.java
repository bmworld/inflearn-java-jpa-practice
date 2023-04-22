package jpaWithApi.jpashop.api;

import jpaWithApi.jpashop.api.orderDto.OrderDto;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderItem;
import jpaWithApi.jpashop.repository.OrderRepository;
import jpaWithApi.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> result = orderRepository.findAllByString(new OrderSearch());
        for (Order order : result) {
            order.getMember().getName();// Proxy 객체 강제 초기화
            order.getDelivery().getAddress(); // Proxy 객체 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems(); // Proxy 객체 강제 초기화
            orderItems.stream().forEach(o -> o.getItem().getName()); // Proxy 객체 강제 초기화
        }
        return result;

    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return collect;

    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();


        List<OrderDto> collect = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return collect;
    }

}
