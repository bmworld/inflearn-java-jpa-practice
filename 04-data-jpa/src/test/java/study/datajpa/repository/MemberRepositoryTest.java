package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import java.util.List;
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


  @DisplayName("basicCRUD")
  @Test
  public void basicCRUDOfSpringDataJPA() throws Exception{
    // Given
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");

    memberRepository.save(member1);
    memberRepository.save(member2);

    Member foundMember1 = memberRepository.findById(member1.getId()).get();
    Member foundMember2 = memberRepository.findById(member2.getId()).get();

    // 단건 조회 건
    assertThat(foundMember1).isEqualTo(member1);
    assertThat(foundMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long countAfterDelete = memberRepository.count();
    assertThat(countAfterDelete).isEqualTo(0);


  }


  @DisplayName("findByNameAndAgeGreaterThanOfSpringDataJPA")
  @Test
  public void findByNameAndAgeGreaterThanOfSpringDataJPA() throws Exception{
    // Given
    createMembers();


    List<Member> result = memberRepository.findByNameAndAgeGreaterThan("USERNAME", 15);

    assertThat(result.get(0).getName()).isEqualTo("USERNAME");
    assertThat(result.get(0).getAge()).isEqualTo(30);
    assertThat(result.size()).isEqualTo(1);
  }

  @DisplayName("find...By에서 ...에 식별하기 위한 내용(설명)이 들어가도 된다. ")
  @Test
  void findHelloooooowBy() {
    createMembers();
    List<Member> all = memberRepository.findHelloooooowBy();
    assertThat(all.size()).isEqualTo(3);

  }

  @DisplayName("findTop`N`...By: N개만큼의 limit Query가 나간다.")
  @Test
  void findTop3HelloBy() {
    List<Member> all = memberRepository.findTop3HelloBy(); // => ... from member member0_ limit 3;
  }

  private void createMembers() {
    Member m1 = new Member("USERNAME", 10);
    Member m2 = new Member("USERNAME", 13);
    Member m3 = new Member("USERNAME", 30);

    memberRepository.save(m1);
    memberRepository.save(m2);
    memberRepository.save(m3);
  }
}
