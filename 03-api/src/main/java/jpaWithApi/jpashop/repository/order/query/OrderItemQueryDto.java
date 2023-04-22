package jpaWithApi.jpashop.repository.order.query;

import jpaWithApi.jpashop.domain.Address;
import jpaWithApi.jpashop.domain.order.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderItemQueryDto {

    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
