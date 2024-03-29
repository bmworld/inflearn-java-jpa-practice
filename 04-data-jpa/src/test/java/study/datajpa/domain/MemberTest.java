package study.datajpa.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  MemberRepository memberRepository;

  @DisplayName("testMemberEntity")
  @Test
  public void testMemberEntity() throws Exception{
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);

    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    // 초기화
    em.flush();
    em.clear();
    List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.getTeam() = " + member.getTeam());

    }

    // Then

  }


  @DisplayName("JpaEventBaseEntity")
  @Test
  public void JpaEventBaseEntity() throws Exception{
    // Given
    Member member = new Member("member1");

    memberRepository.save(member);

    Thread.sleep(1000);

    member.setName("changedName");

    em.flush();
    em.clear();

    // When
    Member foundMember = memberRepository.findById(member.getId()).get();
    // Then

    System.out.println("------ foundMember.getCreateDate() = " + foundMember.getCreateDate());
    System.out.println("------ foundMember.getLastModifiedDate() = " + foundMember.getLastModifiedDate());
    System.out.println("------ foundMember.getCreatedBy() = " + foundMember.getCreatedBy());
    System.out.println("------ foundMember.getLastModifiedBy() = " + foundMember.getLastModifiedBy());

  }

}
