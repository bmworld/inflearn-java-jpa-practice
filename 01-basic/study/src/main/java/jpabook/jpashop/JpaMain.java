package jpabook.jpashop;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.example.Child;
import jpabook.jpashop.domain.example.Parent;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();

    EntityTransaction tx = em.getTransaction();

    tx.begin();


    try {

      Member m1 = new Member();
      m1.setName("bm123");

      Member m2 = new Member();
      m2.setName("bmworld");

      Book book = new Book();
      book.setName("Book1");
      book.setAuthor("bm");


      Order order = new Order();
      Delivery delivery = new Delivery();
      order.setDelivery(delivery);

      em.persist(m1);
      em.persist(m2);
      em.persist(book);
      em.persist(order);


      List<Member> results = em.createQuery("select m from Member m WHERE m.name like '%bm%'", Member.class).getResultList();

      System.out.println("---- results = " + results);
      for (Member member : results) {
        System.out.println("----- member = " + member. getName());

      }


      TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE type(i) = Book", Item.class);
      List<Item> resultList = query.getResultList();
      for (Item item : resultList) {
        System.out.println("---- item = " + item);
      }


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
