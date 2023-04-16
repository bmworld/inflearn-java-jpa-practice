package jpabook.jpashop.service;

import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class) // jnuit과 "spring" 을 엮어서 실행할 때 사용
@SpringBootTest // SpringBoot 을 띄운 상태로 테스트 코드 실행하게 해줌.
@Transactional // TEST 코드일 경우, Test 종류 후, Rollback 실행
public class MemberServiceTest {


  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;


  @DisplayName("회원 가입")
  @Test
  public void 회원가입() throws Exception {
    // Given
    Member member = new Member();
    member.setName("member1");

    // When
    Long savedId = memberService.join(member);


    // Then
    Member foundMember = memberRepository.findOne(savedId);
    assertEquals(member, foundMember);


  }

  @DisplayName("중복_회원_예외")
  @Test(expected = IllegalStateException.class)
  public void 중복_회원_예외() throws Exception {
    // Given
    Member member1 = new Member();
    member1.setName("bmworld");


    Member member2 = new Member();
    member2.setName("bmworld");


    // When
    memberService.join(member1);
    memberService.join(member2); // 여기서 "예외"가 터져야 한다.


    // Then
    fail("예외가 발생해야한다.");

  }
}
