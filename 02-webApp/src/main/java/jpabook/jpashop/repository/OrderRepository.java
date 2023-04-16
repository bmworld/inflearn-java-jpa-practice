package jpabook.jpashop.repository;

import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class,id);
  }


  // TODO : 추후 개발 예정
//  public List<Order> findAll(OrderSearch orderSearch) {}

}
