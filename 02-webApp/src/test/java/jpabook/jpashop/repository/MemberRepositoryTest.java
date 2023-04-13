package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  EntityManager em;

  @Test
  @Rollback(false) // DB에 저장하고 싶을 경우 => false
  public void saveMember() throws Exception {
    // Given
    Member member = new Member();
    member.setUsername("memberA");

    // When
    Long savedId = memberRepository.save(member);
    Member foundMember = memberRepository.find(savedId);

    // Then
    Assertions.assertThat(foundMember.getId()).isEqualTo(member.getId());
    Assertions.assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
    // "영속성 Context" 안에서, 동일한 Entity일 경우, Equal 비교 시, True
    Assertions.assertThat(foundMember).isEqualTo(member);
  }


}
