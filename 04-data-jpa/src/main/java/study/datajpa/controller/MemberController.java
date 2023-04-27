package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberRepository memberRepository;


  @GetMapping("/members/{id}")
  public String findMember(@PathVariable("id") Long id) {
    Member member = memberRepository.findById(id).get();

    return member.getName();
  }

  /**
   * DomainClassConverter
   * <br/>
   * 도메인 클래스 컨버터는 단순 "조회용"으로만 사용해야함.
   * <br>
   * "Transaction이 없는 범위" (= 영속성 Context 밖)에서 Entity를 조회했기 때문에, Entity변경 감지기능이 작동하지 않는다.
   * <br>
   * HTTP 요청은 회원 id를 받지만, Domain Class Converter가 중간에 동작하여 회원 entity 객체를 반환함.
   */
  @GetMapping("/members2/{id}")
  public String findMemberViaDomainClassConverter(@PathVariable("id") Member member) {
    return member.getName();
  }


  /**
   *  - @PostConstruct 사용 시, 의존성 주입이 끝나고 실행됨이 보장되므로 빈의 초기화에 대해서 걱정할 필요가 없다.
   *  <br>
   *  - bean 의 생애주기에서 오직 한 번만 수행된다는 것을 보장한다. (어플리케이션이 실행될 때 한번만 실행됨)
   */
  @PostConstruct
  public void init() {
    for (int i = 0; i < 50; i++) {
      memberRepository.save(new Member(("member" + i + 1)));
    }
  }


  // #################################################################

  /**
   * - PAGEABLE: Spring Data JPA 에서 제공하는 것이 있다.
   */
  @GetMapping("/members")
  public Page<MemberDto> list(@PageableDefault(size=7) Pageable pageable) {
    Page<Member> page = memberRepository.findAll(pageable);
    return page.map(MemberDto::new);
  }
}
