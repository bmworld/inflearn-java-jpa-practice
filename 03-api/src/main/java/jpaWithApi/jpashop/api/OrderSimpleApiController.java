package jpaWithApi.jpashop.api;

import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.repository.OrderRepository;
import jpaWithApi.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;

    }
}
