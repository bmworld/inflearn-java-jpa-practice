package jpabook.jpql;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.DTO.MemberDTO;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {


            Member member = new Member();
            member.setName("memberName1");
            member.setAge(21);
            em.persist(member);

            em.flush();
            em.clear();


            //
            List<MemberDTO> results = em.createQuery("select new jpabook.jpashop.DTO.MemberDTO( m.name, m.age) from Member m", MemberDTO.class)
                    .getResultList();


            MemberDTO dto = results.get(0);
            System.out.println("dto.getUsername() = " + dto.getUsername());
            System.out.println("dto.getUsername() = " + dto.getAge());





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
