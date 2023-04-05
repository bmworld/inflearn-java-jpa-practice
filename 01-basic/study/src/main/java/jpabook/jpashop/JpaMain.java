package jpabook.jpashop;
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


      Child child1 = new Child();

      Child child2 = new Child();

      Parent parent = new Parent();

      parent.addChild(child1);
      parent.addChild(child2);

      em.persist(parent);
      em.persist(child1);
      em.persist(child2);


      em.flush();
      em.clear();


      Parent foundParent = em.find(Parent.class, parent.getId());

      em.remove(foundParent);

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
