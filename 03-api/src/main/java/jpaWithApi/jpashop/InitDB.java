package jpaWithApi.jpashop;

import jpaWithApi.jpashop.domain.Address;
import jpaWithApi.jpashop.domain.delivery.Delivery;
import jpaWithApi.jpashop.domain.item.Book;
import jpaWithApi.jpashop.domain.member.Member;
import jpaWithApi.jpashop.domain.order.Order;
import jpaWithApi.jpashop.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
//        initService.dbInit1();
//        initService.dbInit2();
    }



    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {

            Member member = createMember("member1", "서울", "스트릿", "111");
            Book book1 = createBook("JPA1", 10000, 10);
            Book book2 = createBook("JPA2", 20000, 20);


            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        public void dbInit2() {

            Member member = createMember("member2", "부산", "수영구", "111");
            Book book1 = createBook("JAVA1", 30000, 300);
            Book book2 = createBook("JAVA2", 40000, 400);


            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }


        private static Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String JPA1, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(JPA1);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);

            em.persist(book1);
            return book1;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            em.persist(member);
            return member;
        }
    }



}
