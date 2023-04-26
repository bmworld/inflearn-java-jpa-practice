package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

  @PersistenceContext
  private EntityManager em;

  public Member save(Member member) {
    em.persist(member);
    return member;
  }

  public void delete(Member member) {
    em.remove(member);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }

  public Optional<Member> findById(Long id) {
    Member member = em.find(Member.class, id);
    return Optional.ofNullable(member);
  }


  public long count() {
    return em.createQuery("select count(m) from Member m", Long.class)
        .getSingleResult();
  }


  public Member find(Long id) {
    return em.find(Member.class, id);
  }

  public List<Member> findByNameAndAgeGreaterThan(String name, int age) {
    return em.createQuery("select m" +
            " from Member m" +
            " where m.name = :name and m.age > :age ", Member.class)
        .setParameter("name", name)
        .setParameter("age", age)
        .getResultList();

  }

  public List<Member> findByPage(int age, int offset, int limit) {
    return em.createQuery("select m from Member m where m.age = :age order by m.name desc", Member.class)
        .setParameter("age", age)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public long totalCountByAge(int age) {
    return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
        .setParameter("age", age)
        .getSingleResult()
        ;
  }

  public int bulkAgePlus(int age) {
    return em.createQuery("UPDATE Member m SET m.age = m.age +1 WHERE m.age >= :age")
        .setParameter("age", age)
        .executeUpdate();

  }

}
