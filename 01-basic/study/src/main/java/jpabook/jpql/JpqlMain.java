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

            for (int i = 0; i < 2; i++) {



                Team team = new Team();
                team.setName("name" + i);
                em.persist(team);


                Member member = new Member();
                member.setName(i == 0 ? "관리자" : ("username" + i));
                member.setAge(i);
                member.setRoleType(i == 0 ? RoleType.ADMIN : RoleType.USER);


                member.changeTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();


            String query = "select size(t.members) from Team t ";

            List<Integer> result = em.createQuery(query, Integer.class)
                    .getResultList();


            for (Integer s : result) {
                System.out.println("Result = " + s);
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
