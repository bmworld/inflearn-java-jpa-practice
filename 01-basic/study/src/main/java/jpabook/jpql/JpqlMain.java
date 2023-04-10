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


            String CaseQuery = "select " +
                    "CASE WHEN m.age <= 10 then '학생요금' " +
                    "     WHEN m.age >= 60 then '경로요금' " +
                    "     ELSE '일반요금' END " +
                    "FROM Member m";

            List<String> result = em.createQuery(CaseQuery, String.class)
                    .getResultList();


            for (String s : result) {
                System.out.println("CASE1 Result = " + s);
            }



            String CaseQuery2 = "select nullif(m.name, '관리자') FROM Member m";

            List<String> result2 = em.createQuery(CaseQuery2, String.class)
                    .getResultList();


            for (String s : result2) {
                System.out.println("CASE2 Result = " + s);
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
