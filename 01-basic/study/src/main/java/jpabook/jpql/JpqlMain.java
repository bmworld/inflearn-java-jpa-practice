package jpabook.jpql;

import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.DTO.MemberDTO;
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
                member.setName("name" + i);
                member.setAge(i);
                member.setRoleType(i == 0 ? RoleType.ADMIN : RoleType.USER);


                member.changeTeam(team);

                em.persist(member);
            }

            em.flush();
            em.clear();


            String query = "select m.name, 'HELLO', true from Member m " +
                    "where m.roleType = :roleType";

            List<Object[]> results = em.createQuery(query)
                    .setParameter("roleType", RoleType.USER)
                    .getResultList();

            for (Object[] objects : results) {
                System.out.println("---- objects[n] = " + objects[0]);
                System.out.println("---- objects[n] = " + objects[1]);
                System.out.println("---- objects[n] = " + objects[2]);
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
