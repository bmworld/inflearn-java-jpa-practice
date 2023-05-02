package study.querydsl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnotherMemberDto {
  private String username;
  private int age;

  public AnotherMemberDto(String name, int age) {
    this.username = name;
    this.age = age;
  }
}
