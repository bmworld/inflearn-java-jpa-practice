package jpabook.jpashop.domain;

import jpabook.jpashop.domain.example.embeddedType.Address;
import jpabook.jpashop.domain.example.embeddedType.AddressEntity;
import jpabook.jpashop.domain.example.embeddedType.Employee;
import jpabook.jpashop.domain.example.embeddedType.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.*;
import java.util.stream.Collectors;

public class ValueMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();


        try {

            Employee emp = new Employee();
            emp.setUsername("bm");



            AddressEntity addressEntity1 = new AddressEntity("city", "street", "zipcode");
            AddressEntity addressEntity2 = new AddressEntity("city2", "street2", "zipcode2");
            emp.setHomeAddress(new Address("homeCity1", "homeStreet1", "homeZipcode1"));
            emp.getAddressHistory().add(addressEntity1);
            emp.getAddressHistory().add(addressEntity2);

            Set<String> favoriteFoods = new HashSet<>();
            favoriteFoods.add("cake");
            favoriteFoods.add("manna");

            emp.setFavoriteFoods(favoriteFoods);


            em.persist(emp);


            em.flush();
            em.clear();






            System.out.println("================================================");

            Employee foundEmp = em.find(Employee.class, emp.getId());


            AddressEntity foundAddressEntity = foundEmp.getAddressHistory().get(1);
            System.out.println("----- foundAddressEntity = " + foundAddressEntity);
            System.out.println("----- foundAddressEntity.getAddress().getCity() = " + foundAddressEntity.getAddress().getCity());




            // Immutable 객체 변경: 통째로 갈아끼워야함.
//            Address beforeAddr = foundEmp.getHomeAddress();
//            foundEmp.setHomeAddress(new Address("newCity", beforeAddr.getStreet(), beforeAddr.getZipcode()));


            // 값 타입 > Primitive Collection 변경: 통째로 Hash를 변경해야함.
            foundEmp.getFavoriteFoods().remove("cake");
            foundEmp.getFavoriteFoods().add("coffee");
            foundEmp.getFavoriteFoods().add("earl grey");


            // 값 타입 > Embedded Collection 변경 =====> 참조 객체 삭제하면 절대. 안.됨. ==> SideEffect 주의
//            foundEmp.getAddressHistory().remove(new Address("city", "street", "zipcode"));
//            foundEmp.getAddressHistory().add(new Address("city3", "street3", "zipcode3"));



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
