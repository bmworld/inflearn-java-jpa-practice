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


            for (int i = 0; i < 3; i++) {




                Member member = new Member();
                member.setName(i == 0 ? "관리자" : ("USER-" + i));
                member.setRoleType(i == 0 ? RoleType.ADMIN : RoleType.USER);


                member.changeTeam(i == 0 ? team1 : team2);

                em.persist(member);
            }

            em.flush();
            em.clear();


            List<Member> resultList = em.createNamedQuery("Member.findByName", Member.class)
                    .setParameter("name", "관리자")
                    .getResultList();


//
//            String query = "select m FROM Member m WHERE m.team = :team";
//
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .setParameter("team", team2)
//                    .getResultList();


            System.out.println("----------------------------------------");

            System.out.println("results = " + resultList);

            for (Member s : resultList) {
                System.out.println("member.getName() = " + s.getName());

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
