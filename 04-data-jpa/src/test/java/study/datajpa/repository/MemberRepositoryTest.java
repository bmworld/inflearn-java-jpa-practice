package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Rollback(false)
@Transactional
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @DisplayName("testMemberUsingByDataJpaRepository")
  @Test
  public void testMemberUsingByDataJpaRepository() throws Exception{
    // Given
    Member member = new Member("member1");
    Member savedMember = memberRepository.save(member);
    Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
    Member foundMember = null;
    if (optionalMember.isPresent()) {
      foundMember = optionalMember.get();

    }

    assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
    assertThat(foundMember.getName()).isEqualTo(savedMember.getName());
    assertThat(foundMember).isEqualTo(savedMember);

  }

}
