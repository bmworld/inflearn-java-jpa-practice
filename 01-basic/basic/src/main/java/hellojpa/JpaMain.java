package hellojpa;

import hellojpa.member.RoleType;
import org.hibernate.FlushMode;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
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

    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();


    try {



      // ####################### JPA 기본 #####################


      // ** EXAMPLE > CREATE
      Member member = Member.of( "m2", 20, RoleType.USER, "HelloWorld");
      member.setRoleType(RoleType.ADMIN);
//      m2.setName("tooMuchLongerNametooMuchLongerNametooMuchLongerName");



      Guest guest = new Guest();
      guest.setRoleType(RoleType.GUEST);


      System.out.println("==============");
      em.persist(member); // JPA에 Member 객체 등록.
      em.persist(guest); // JPA에 Member 객체 등록.
      System.out.println("--- member.getId() = " + member.getId());
      System.out.println("--- guest.getId() = " + guest.getId());
      System.out.println("==============");




      // ** EXAMPLE > SELECT
//      Member member = em.find(Member.class, 1L);
      // ! JPA를 사용해서, Entity를 가져오면, JPA의 관리대상이 된다.
      // 따라서, JPA 에 등록된 ENTITY는, JAVA Collection 처럼 DB를 UPDATE할 수 있게 하는 QUERY를 날린다.
      // 따라서 Transaction 시점에 Entity를 점검해서 변경사항이 존재하면,
      //  DB 쿼리를 만들어서 날려주고 transaction을 닫는다.




      // ** EXAMPLE > UPDATE
//      member.setName("bm1");




      // ** EXAMPLE > DELETE
//      Member member = em.find(Member.class, 3L);
//      System.out.println("member = " + member.getId());
//      System.out.println("member = " + member.getName());
//      em.remove(member);







      // ** EXAMPLE > JPQL (Java Persistence Query Language)
      // :Entity Class 조회에 사용하는 객체지향 쿼리다.
      // ! 결국 Query또한  DB Qeury 중심이 되면, JPA가 지향하는 객체지향 설계에서 벗어나게 된다.
      // 따라서 JPQL은 객체를 중심으로 Query 만들고, DB query 변환하도록 만들어진 거다.
//      List<Member> result = em.createQuery("SELECT m FROM Member as m", Member.class)
//        .setFirstResult(0) // 페이지네이션에 활용 (가져올 데이터 인덱스 )
//        .setMaxResults(10) // 페이지네이션에 활용 (결과 개수 / 인덱스 X)
//        .getResultList();
//      for (Member mb: result) {
//        System.out.println("------ member.name = " + mb.getName());
//      }







      // ################## 영속성 Context 학습 ##################
      // 영속 (insert query 는 commit 시점에 날라간다.)



      // 비영속
//      Member member = new Member();
//      member.setId(3L);
//      member.setName("bmworld");


//      System.out.println(" ---- BEFORE");
//      em.persist(member);
//      System.out.println(" ---- AFTER");


//      Member firstFindMember = em.find(Member.class, 1L);  // Entity를 처음 조회할 때는, EntityManager에 캐시된 값이 없으므로, DB Query를 날린다.
//      System.out.println("---- firstFindMember" + firstFindMember.getId());
//      //
//      Member secondFindMember = em.find(Member.class, 1L); // 동일한 Entity를 두 번째 조회할 때는, 1차 캐시에 저장된 값이 있으므로, DB query를 날리지 않고, 캐시 데이터에서 가져온다.
//      System.out.println("---- secondFindMember" + secondFindMember.getId());







//      // ################## 영속성 Entity 동일성 보장 ##################
//      System.out.println("Is The Same Entity = " + (firstFindMember == secondFindMember) +" ( 영속성 Context 1차 캐시에서 두 객체를 비교한 결과) ");







      // #################### DB Query 최적화 ####################
      // => DB Query 쌓아두었다가, 한 방에 보낼 수 있다. (*최대 query 개수: Value of jdbc.batch_size )

//      Member m1 = new Member(100L, "m1");
//      Member m2 = new Member(101L, "m2");
//      em.persist(m1);
//      em.persist(m2);

      // #################### JPA 변경감지 => Java Collection 처럼 Entity값을 변경하면, DB update query 날아간다. ####################
//      Member m1 = em.find(Member.class, 100L);
//      m1.setName("변경된 이름-별도의 flush를 하지 않았다.");



      // #################################################################
//      Member m = em.find(Member.class, 100L);


//      em.clear(); // 영속성 context를 완전히 초기화 => 영속성 context에서 관리하는 entity가 없으므로, 아래에서 신규로 영속성 context에 등록하지 않은 Entity의 변경사항은 DB에 반영되지 않는다.
//      em.flush(); // 직접호출 (커밋을 이 시점에서 수동으로 날릴 수 있다.)
//      Member m2 = em.find(Member.class, 100L);
//      System.out.println("m2 = " + m2);



      // ################### Transaction ###################
      // Commit 시점에 영속성 Context에 Entity의 마지막 상태를 토대로, Query 날림. (commit 이전에 remove => persist 했다면, remove 미적용)
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      // ##########################################
      // close the database
      // ! Entity Manager는
      //   자원을 다 쓰고 나면 반드시 닫는다.
      System.out.println("----- em.close() ");
      em.close();
    }


    // ##########################################
    System.out.println("----- emf.close()");
    emf.close();

  }
}
