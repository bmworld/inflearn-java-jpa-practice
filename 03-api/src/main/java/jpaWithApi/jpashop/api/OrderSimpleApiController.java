package jpaWithApi.jpashop.api;

import jpaWithApi.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto;
import jpaWithApi.jpashop.api.orderDto.SimpleOrderResponseDto;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.repository.OrderRepository;
import jpaWithApi.jpashop.repository.OrderSearch;
import jpaWithApi.jpashop.repository.order.simpleQuery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * X To One (ManyToOne, OneToOne) => 양방향 가능 시, 두 Entity 중 하나의 관계를 끊어줘야함 ( @JsonIgnore )
 * Order
 * Order -> Member
 * Order -> Delivery
 */


@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderResponseDto> ordersV2() {
        // 1 + N + N 문제발생 ( 1: order  / N: order.member / N: order.delivery
        return orderRepository.findAllByString(new OrderSearch())
            .stream()
            .map(SimpleOrderResponseDto::new)
            .collect(toList());
    }



    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderResponseDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(SimpleOrderResponseDto::new)
                .collect(toList());
    }


    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
