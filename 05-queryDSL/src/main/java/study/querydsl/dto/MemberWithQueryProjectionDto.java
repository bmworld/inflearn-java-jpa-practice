package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberWithQueryProjectionDto {
  private String name;
  private int age;

  @QueryProjection
  public MemberWithQueryProjectionDto(String name, int age) {
    this.name = name;
    this.age = age;
  }
}
