package jpabook.jpashop;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Member;

import javax.persistence.*;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    // ** JPA 에서 모든 DB 작업은 `transaction` 단위 내에서 실행해야 한다. > BEGIN
    EntityTransaction tx = em.getTransaction();
    tx.begin();




    try {

      Movie movie = new Movie();
      movie.setDirector("A director");
      movie.setActor("A Actor");
      movie.setName("타이타닉");
      movie.setPrice(10000);
      em.persist(movie);


      em.flush();
      em.clear();


      Movie foundMovie = em.find(Movie.class, movie.getId());
      System.out.println("foundMovie = " + foundMovie);


      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
