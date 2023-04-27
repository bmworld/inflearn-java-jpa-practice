package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.domain.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController{

  private final MemberRepository memberRepository;



  @GetMapping("/members/{id}")
  public String findMember(@PathVariable("id") Long id) {
    Member member = memberRepository.findById(id).get();

    return member.getName();
  }

  /**
   * HTTP 요청은 회원 id를 받지만, Domain Class Converter가 중간에 동작하여 회원 entity 객체를 반환함.
   */
  @GetMapping("/members2/{id}")
  public String findMemberViaDomainClassConverter(@PathVariable("id") Member member) {
    return member.getName();
  }

  /**
   * DomainClassConverter
   * <br/>
   * 도메인 클래스 컨버터는 단순 "조회용"으로만 사용해야함.
   * <br>
   * "Transaction이 없는 범위" (= 영속성 Context 밖)에서 Entity를 조회했기 때문에, Entity변경 감지기능이 작동하지 않는다.
   */
  @PostConstruct
  public void init() {
    memberRepository.save(new Member("userA"));
  }


}
