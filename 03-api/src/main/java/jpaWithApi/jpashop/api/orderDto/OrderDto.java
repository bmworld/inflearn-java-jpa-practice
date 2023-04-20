package jpaWithApi.jpashop.api.orderDto;


import jpaWithApi.jpashop.domain.Address;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderItem;
import jpaWithApi.jpashop.domain.order.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order o) {
        orderId = o.getId();
        name = o.getMember().getName();
        orderDate = o.getOrderDate();
        orderStatus = o.getStatus();
        address = o.getDelivery().getAddress();
        orderItems = o.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
