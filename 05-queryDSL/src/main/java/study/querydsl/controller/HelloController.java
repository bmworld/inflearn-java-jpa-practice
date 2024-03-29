package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HelloController {
  
  private final MemberJpaRepository memberJpaRepository;
  private final MemberRepository memberRepository;
  
  @GetMapping("/")
  public String hello() {
    return "hello";
  }
  
  
  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCondition cond) {
    return memberJpaRepository.searchMemberTeamDtoByWhereCond(cond);
  }
  
  @GetMapping("/v2/members")
  public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition cond, Pageable pageable) {
    return memberRepository.searchPageSimple(cond, pageable);
  }
  
  @GetMapping("/v3/members")
  public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition cond, Pageable pageable) {
    return memberRepository.searchPageComplex(cond, pageable);
  }
  

}
