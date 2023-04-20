package jpaWithApi.jpashop.api.orderDto;

import jpaWithApi.jpashop.domain.Address;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOrderResponseDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;

  public SimpleOrderResponseDto(Order order) {
    this.orderId = order.getId();
    this.name = order.getMember().getName();// LAZY 로딩 => order.member값을 꺼내오려는 순간. 실제 Query 나감.
    this.orderDate = order.getOrderDate();
    this.orderStatus = order.getStatus();
    this.address = order.getDelivery().getAddress(); // LAZY 초기화
  }
}
