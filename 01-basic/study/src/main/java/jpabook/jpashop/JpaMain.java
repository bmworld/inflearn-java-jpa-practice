package jpabook.jpashop;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.member.Member;

import javax.persistence.*;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();




    try {

      Member member = new Member();
      member.setName("m1");
      em.persist(member);

      Team team = new Team();
      team.setName("TeamA");

      team.getMembers().add(member);
      em.persist(team);



      Locker locker = new Locker();

      member.setLocker(locker);
      Member findedMember = locker.getMember();
      System.out.println("findedMember = " + findedMember);
      em.persist(locker);



      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
