package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    System.out.println("JpaMain > main");


    // #########################################################################
    // persistence.xml <persistence-unit name="hello"> 해당 name을 입력한다.
    // EntityManagerFactory => 딱 1개의 Instance 존재해야 함.
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    // #########################################################################
    // DB connection & Close 의 하나의 싸이클마다, Entity Manager를 만들어 줘야 한다.
    // ! EntityManager는 여러 Thread에서 공유 금지.
    // ! single thread에서 1회용 젓가락 처럼 쓰고 버려야한다.
    EntityManager em = emf.createEntityManager();

    // #########################################################################
    System.out.println("> Execute DB SQL Query");



    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();


    try {
      // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > COMMIT
      // ** EXAMPLE > CREATE
//      Member member = new Member();
//      member.setId(2L);
//      member.setName("john");
//      em.persist(member); // JPA에 Member 객체 등록.


      // ** EXAMPLE > SELECT
//      Member member = em.find(Member.class, 1L);
      // ! JPA를 사용해서, Entity를 가져오면, JPA의 관리대상이 된다.
      // 따라서, JPA 에 등록된 ENTITY는, JAVA Collection 처럼 DB를 UPDATE할 수 있게 하는 QUERY를 날린다.
      // 따라서 Transaction 시점에 Entity를 점검해서 변경사항이 존재하면,
      //  DB 쿼리를 만들어서 날려주고 transaction을 닫는다.
//      System.out.println("findMember.id = " + member.getId());
//      System.out.println("findMember.name = " + member.getName());


      // ** EXAMPLE > UPDATE
//      member.setName("bm2");

      // ** EXAMPLE > DELETE
//      em.remove(member);


      // ** EXAMPLE > JPQL (Java Persistence Query Language)
      // :Entity Class 조회에 사용하는 객체지향 쿼리다.
      // ! 결국 Query또한  DB Qeury 중심이 되면, JPA가 지향하는 객체지향 설계에서 벗어나게 된다.
      // 따라서 JPQL은 객체를 중심으로 Query 만들고, DB query 변환하도록 만들어진 거다.
      List<Member> result = em.createQuery("SELECT m FROM Member as m", Member.class)
        .setFirstResult(1)
        .setMaxResults(10)
        .getResultList();
      for (Member mb: result) {
        System.out.println("member.name = " + mb.getName());
      }

      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      // #########################################################################
      // close the database
      // ! Entity Manager는
      //   자원을 다 쓰고 나면 반드시 닫는다.
      em.close();
    }

    // ** EXAMPLE
    // Hibernate:
    //  /* insert hellojpa.Member  */ // persistence.xml > hibernate.use_sql_comments (주석보여줌)
    //  insert                        // persistence.xml > hibernate.show_sql (query보여줌)
    //  into                          // persistence.xml > hibernate.format_sql (포맷 팅함)
    //  Member
    //  (name, id)
    //  values
    //  (?, ?)


//    SELECT
//        m
//    FROM
//        Member m select
//    member0_.id as id1_0_,
//      member0_.name as name2_0_`
//    from
//    Member member0_ limit ? offset ?




    // #########################################################################
    System.out.println("> Entity Manager Factory to be closed");
    emf.close();

  }
}
