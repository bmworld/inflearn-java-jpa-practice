package jpaWithApi.jpashop.service;

import jpaWithApi.jpashop.domain.Address;
import jpaWithApi.jpashop.domain.item.Book;
import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderStatus;
import jpaWithApi.jpashop.exception.NotEnoughStockException;
import jpaWithApi.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

  @Autowired
  EntityManager em;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderRepository orderRepository;

  @DisplayName("상품_주문")
  @Test
  public void 상품_주문() throws Exception{
    // Given

    int itemPrice = 10000;
    int itemTotalAmount = 10;
    String itemName = "영도 블루스";
    int orderCount = 2;


    Member member = createMember();
    Book item = createBook(itemPrice, itemTotalAmount, itemName);

    // When
    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

    // Then
    Order foundOrder = orderRepository.findOne(orderId);

    assertEquals("상품 주문 시, 주문 상태는 ORDER", OrderStatus.ORDER, foundOrder.getStatus());
    assertEquals("주문한 상품 종류 수 검증", 1, foundOrder.getOrderItems().size());
    assertEquals("주문 가격 = 가격 * 수량", itemPrice * orderCount, foundOrder.getTotalPrice());
    assertEquals("주문 수량만큼 재고가 줄어야 한다", itemTotalAmount - orderCount, item.getStockQuantity());
  }


  @DisplayName("상품주문_재고수량초과")
  @Test(expected = NotEnoughStockException.class)
  public void 상품주문_재고수량초과() throws Exception{
    // Given
    int itemPrice = 10000;
    int itemTotalAmount = 10;
    int orderCount = 11;
    String itemName = "영도 블루스";

    Member member = createMember();
    Book item = createBook(itemPrice, itemTotalAmount, itemName);


    // When
    orderService.order(member.getId(), item.getId(), orderCount);

    // Then
    fail("재고 수량 부족 예외(NotEnoughStockException)가 발생해야 한다.");

  }


  @DisplayName("주문_취소")
  @Test
  public void 주문_취소() throws Exception{
    // Given
    int itemPrice = 10000;
    int itemTotalAmount = 10;
    int orderCount = 2;
    String itemName = "영도 JPA";

    Member member = createMember();
    Book item = createBook(itemPrice, itemTotalAmount, itemName);
    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

    // When
    orderService.cancelOrder(orderId);

    // Then
    Order foundOrder = orderRepository.findOne(orderId);

    assertEquals("주문 취소 시, 주문 상태는 CANCEL", OrderStatus.CANCEL, foundOrder.getStatus());
    assertEquals("주문이 취소된 상품은 재고가 복구된다.", itemTotalAmount, item.getStockQuantity());

  }



  private Book createBook(int itemPrice, int itemTotalAmount, String name) {
    Book book = new Book();
    book.setName(name);
    book.setPrice(itemPrice);
    book.setStockQuantity(itemTotalAmount);
    em.persist(book);
    return book;
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "판교", "12345"));
    em.persist(member);
    return member;
  }
}
