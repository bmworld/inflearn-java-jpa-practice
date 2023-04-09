package jpabook.jpql;

import jpabook.jpashop.domain.Member;

import javax.persistence.*;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {


            Member member = new Member();
            member.setName("memberName1");
            em.persist(member);


            // 타입정보를 받을 수 있을 때
            Member foundMember = em.createQuery("select m from Member m where m.name= :name", Member.class)
                    .setParameter("name", "memberName1")
                    .getSingleResult();

            Member foundMember2 = em.createQuery("select m from Member m where m.name=?1", Member.class)
                    .setParameter(1, "memberName1")
                    .getSingleResult();

//            System.out.println("foundMember.getName() = " + foundMember.getName());
            System.out.println("foundMember2.getName() = " + foundMember2.getName());


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
