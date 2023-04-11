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


            String query = "select t FROM Team t";

            List<Team> results = em.createQuery(query, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(3)
                    .getResultList();


            System.out.println("----------------------------------------");


            for (Team s : results) {
                System.out.println("---- Team = " + s.getName() + " | " + s.getMembers().size());
                List<Member> members = s.getMembers();
                for (Member member : members) {
                    System.out.println("member.getName() = " + member.getName());
                }

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
