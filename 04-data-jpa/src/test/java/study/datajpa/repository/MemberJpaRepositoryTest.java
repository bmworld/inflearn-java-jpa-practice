package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    Member member = new Member("m2");
    Member savedMember = memberJpaRepository.save(member);
    Member foundMember = memberJpaRepository.find(savedMember.getId());
    // When


    // Then
    assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
    assertThat(foundMember.getName()).isEqualTo(savedMember.getName());
    assertThat(foundMember).isEqualTo(savedMember);

  }

}
