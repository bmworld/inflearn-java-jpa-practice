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
                team.setName("teamName" + i);
                em.persist(team);


                Member member = new Member();
                member.setName("memberName" + i);
                member.setAge(i);

                member.changeTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();





            List<Member> results = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result.size() = " + results.size());
            for (Member m1 : results) {
                System.out.println("m1 = " + m1);
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
