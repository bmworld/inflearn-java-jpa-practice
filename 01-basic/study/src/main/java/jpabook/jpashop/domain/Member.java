package jpabook.jpashop.domain;


import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity
public class Member {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "MEMBER_ID")
  private Long id;

  @Column(name = "USERNAME", nullable = false)
  private String name;


//  @Column(name="TEAM_ID")
//  private Long teamId;

  @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY일 경우. Query가 분리 되어서 날아간다.
  @JoinColumn(name="TEAM_ID")
  private Team team; // DB관점에서 해당 키의 '주인'이 누구인지,'종' 테이블에서 해당 매핑을 해준다.

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

  public Team getTeam() {
    return team;
  }

  // JPA에서 FK의 양방향 연관관계를 반드시 이해해야한다.
  public void setTeam(Team team) {
    this.team = team;
  }


//  public void changeTeam(Team team) {
//    this.team = team;
//    team.getMembers().add(this);
//  }


}
