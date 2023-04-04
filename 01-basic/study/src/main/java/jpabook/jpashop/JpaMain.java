package jpabook.jpashop;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;
import org.hibernate.Hibernate;

import javax.persistence.*;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();


    try {

      Team team = new Team();
      team.setName("teamA");

      Member member = new Member();
      member.setName("bm");
      member.setTeam(team);

      em.persist(team);
      em.persist(member);


      em.flush();
      em.clear();


      Member m = em.find(Member.class, member.getId());
      System.out.println("m.getTeam().class() = " + m.getTeam().getClass());


      System.out.println("-----");
      m.getTeam().getName();
      System.out.println("-----");


      Hibernate.initialize(m);
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
