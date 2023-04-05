package jpabook.jpashop;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.example.Child;
import jpabook.jpashop.domain.example.Parent;

import javax.persistence.*;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();

    EntityTransaction tx = em.getTransaction();

    tx.begin();


    try {

      Book book = new Book();
      book.setName("Book1");
      book.setAuthor("bm");


      Order order = new Order();
      Delivery delivery = new Delivery();
      order.setDelivery(delivery);

      em.persist(book);
      em.persist(order);




      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      e.printStackTrace();
    } finally {
      em.close();
    }
    emf.close();
  }
}
