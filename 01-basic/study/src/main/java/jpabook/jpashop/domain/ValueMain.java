package jpabook.jpashop.domain;

import jpabook.jpashop.domain.example.embeddedType.Address;
import jpabook.jpashop.domain.example.embeddedType.Employee;
import jpabook.jpashop.domain.example.embeddedType.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ValueMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();


        try {
            Address address1 = new Address("city", "street", "zipcode");
            Address address2 = new Address("city", "street", "zipcode");


//            Employee employee = new Employee();
//            employee.setUsername("bm");
//
//            employee.setHomeAddress(address1);
//
//            employee.setPeriod(new Period());
//
//            em.persist(employee);


            System.out.println("address1 == address2 : " + (address1 == address2));
            System.out.println("address1.equals(address2) : " + address1.equals(address2));



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
