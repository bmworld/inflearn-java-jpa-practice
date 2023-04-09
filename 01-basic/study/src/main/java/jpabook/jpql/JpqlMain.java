package jpabook.jpql;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.DTO.MemberDTO;
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

            for (int i = 0; i < 2; i++) {



                Team team = new Team();
                team.setName("name" + i);
                em.persist(team);


                Member member = new Member();
                member.setName("name" + i);
                member.setAge(i);

                member.changeTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();


            String query = "select m from Member m left join Team t on m.name = t.name";

            List<Member> results = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member m1 : results) {
                System.out.println("---- member = " + m1);
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
