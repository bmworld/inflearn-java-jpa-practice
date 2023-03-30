package jpabook.jpashop;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();




    // T
    Team team = new Team();
    team.setName("TeamA");
    em.persist(team);

    Team team2 = new Team();
    team2.setName("TeamB");
    em.persist(team2);




    // M
    Member member = new Member();
    member.setName("member1");
//    member.setTeam(team); // <-- 양방향 연관관계가 필요한 변경이기에,  getter setter 관례에 의한 것이 아닌 메서드 이름 & 로직 사용

//    member.changeTeam(team); // <-- Member / Team 양방향 연관관계 시, 둘 중 하나만 '추가'메서드를 사용하시라 ( 헷갈리지 않도록. )
    em.persist(member);


    team.addMember(member);

    em.flush();
    em.clear();


    Team foundTeam = em.find(Team.class, team.getId());
    System.out.println("foundTeam = " + foundTeam);
    Member findedMember = em.find(Member.class, member.getId());


    List<Member> members = findedMember.getTeam().getMembers();
    System.out.println();
    for (Member m : members) {
      System.out.println("---- member = " + m.getName());
    }

    try {
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
