package study.querydsl.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
  // 검색조건: 회원명, 팀명, 나이
  private String name;
  private String teamName;
  private Integer ageGoe;
  private Integer ageLoe;
  
  
}
