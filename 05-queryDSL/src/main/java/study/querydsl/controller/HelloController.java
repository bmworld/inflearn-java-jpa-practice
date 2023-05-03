package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HelloController {
  
  private final MemberJpaRepository memberJpaRepository;
  
  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCondition cond) {
    return memberJpaRepository.searchMemberTeamDtoByWhereCond(cond);
  }
  
  @GetMapping("/")
  public String hello() {
    return "hello";
  }
}
