package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

  @Id
  @GeneratedValue
  @Column(name = "TEAM_ID")
  private Long id;
  private String name;

  // ! 겍체의 양방향은 서로 다른 단방향(각각의 참조) 2개를 합친 결과다.(회원 -> 팀 + 팀-> 회원)
  //  참조가 2개라면.....
  //  외래키를 관리하는 녀석은 둘 중 하나여야 한다.


  // ! 테이블의 양방향은, FK(Foreign Key) 하나로, 두 테이블의 연관관계가 한방에 끝난다. (테이블 하나로 합채)
  @OneToMany(mappedBy = "team") // JPA의 핵심 > 객체와 테이블 간에 연관관계 맺는 차이점을 이해해야한다. 반드시.
  private List<Member> members = new ArrayList<Member>();

  public List<Member> getMembers() {
    return members;
  }

  public void setMembers(List<Member> members) {
    this.members = members;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
