package study.querydsl.jpql;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Hello;
import study.querydsl.domain.QHello;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class QuerydslApplicationTests {

  @PersistenceContext
  EntityManager em;

  @Test
  void contextLoads() {
    Hello hello = new Hello();
    hello.setName("Hello");
    em.persist(hello);

    JPAQueryFactory query = new JPAQueryFactory(em);
    QHello qHello = QHello.hello;

    Hello result = query.selectFrom(qHello)
        .fetchOne();

    System.out.println("result = " + result);
    assertThat(result).isEqualTo(hello);
    assertThat(result.getId()).isEqualTo(hello.getId());
  }

}
