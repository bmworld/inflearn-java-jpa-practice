package jpaWithApi.jpashop.service;

import jpaWithApi.jpashop.domain.delivery.Delivery;
import jpaWithApi.jpashop.domain.item.Item;
import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderItem;
import jpaWithApi.jpashop.repository.ItemRepository;
import jpaWithApi.jpashop.repository.MemberRepository;
import jpaWithApi.jpashop.repository.OrderRepository;
import jpaWithApi.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;

  /**
   * 주문
   */
  @Transactional
  public Long order(Long memberId, Long itemId, int count) {

    // Entity 조회
    Member member = memberRepository.findOne(memberId);
    Item item = itemRepository.findOne(itemId);

    // 배송정보 생성
    Delivery newDelivery = new Delivery();
    newDelivery.setAddress(member.getAddress());

    // 주문 상품 생성
    OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

    // 주문 생성
    Order order = Order.createOrder(member, newDelivery, orderItem);

    // 주문 저장
    orderRepository.save(order);

    return order.getId();
  }


  /**
   * 주문 취소
   * @param orderId: 주문 ID
   */
  @Transactional
  public void cancelOrder(Long orderId) {
    // 주문 Entity 조회
    Order order = orderRepository.findOne(orderId);
    order.cancel();
  }

  public List<Order> findOrders(OrderSearch orderSearch) {
    return orderRepository.findAllByString(orderSearch);
  }


}
