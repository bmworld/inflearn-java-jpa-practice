package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @DisplayName("testMember")
  @Test
  public void testMember() throws Exception {
    // Given
    Member member = new Member("m1");
    Member savedMember = memberJpaRepository.save(member);
    Member foundMember = memberJpaRepository.find(savedMember.getId());
    // When


    // Then
    assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
    assertThat(foundMember.getName()).isEqualTo(savedMember.getName());
    assertThat(foundMember).isEqualTo(savedMember);

  }


  @DisplayName("basicCRUD")
  @Test
  public void basicCRUD() throws Exception{
    // Given
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");

    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    Member foundMember1 = memberJpaRepository.findById(member1.getId()).get();
    Member foundMember2 = memberJpaRepository.findById(member2.getId()).get();

    // 단건 조회 건
    assertThat(foundMember1).isEqualTo(member1);
    assertThat(foundMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberJpaRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberJpaRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberJpaRepository.delete(member1);
    memberJpaRepository.delete(member2);

    long countAfterDelete = memberJpaRepository.count();
    assertThat(countAfterDelete).isEqualTo(0);


  }


  @DisplayName("findByNameAndAgeGreaterThan")
  @Test
  public void findByNameAndAgeGreaterThan() throws Exception{
    // Given
    Member m1 = new Member("USERNAME", 10);
    Member m2 = new Member("USERNAME", 13);
    Member m3 = new Member("USERNAME", 30);

    memberJpaRepository.save(m1);
    memberJpaRepository.save(m2);
    memberJpaRepository.save(m3);


    List<Member> result = memberJpaRepository.findByNameAndAgeGreaterThan("USERNAME", 15);

    assertThat(result.get(0).getName()).isEqualTo("USERNAME");
    assertThat(result.get(0).getAge()).isEqualTo(30);
    assertThat(result.size()).isEqualTo(1);
  }

}
