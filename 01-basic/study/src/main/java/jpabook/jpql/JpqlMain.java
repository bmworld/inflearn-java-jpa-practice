package jpabook.jpql;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.RoleType;
import jpabook.jpashop.domain.Team;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {



            Team team1 = new Team();
            team1.setName("TEAM-0");
            em.persist(team1);

            Team team2 = new Team();
            team2.setName("TEAM-1");
            em.persist(team2);


            Member targetMember = null;

            for (int i = 0; i < 3; i++) {




                Member member = new Member();
                member.setName(i == 0 ? "관리자" : ("USER-" + i));
                member.setRoleType(i == 0 ? RoleType.ADMIN : RoleType.USER);


                member.changeTeam(i == 0 ? team1 : team2);

                em.persist(member);

                if (i == 0) {
                    targetMember = member;
                }

            }

            em.flush();
            em.clear();


            String query = "update Member m set m.age = 20";

            int resultCount = em.createQuery(query)
                    .executeUpdate();


            em.clear(); // DB 벌크쿼리 수행된 후, 영속성 context와 동기화가 안된다.
            // 따라서, 영속성 context 비운 후, 다시 시작.

            Member foundMember = em.find(Member.class, targetMember.getId());
            System.out.println("foundMember.getAge() = " + foundMember.getAge());


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
