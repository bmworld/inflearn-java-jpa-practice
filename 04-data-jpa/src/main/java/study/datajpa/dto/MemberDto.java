package study.datajpa.dto;

import lombok.Data;

@Data
public class MemberDto {
  private Long id;
  private String userName;
  private String teamName;


  public MemberDto(Long id, String username, String teamName) {
    this.id = id;
    this.userName = username;
    this.teamName = teamName;
  }
}
