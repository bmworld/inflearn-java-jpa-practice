package jpabook.jpashop.domain.member;

import jpabook.jpashop.domain.Locker;
import jpabook.jpashop.domain.MemberProduct;
import jpabook.jpashop.domain.Product;
import jpabook.jpashop.domain.Team;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "MEMBER_ID")
  private Long id;
  private String name;

  @ManyToOne
  @JoinColumn(name="TEAM_ID", insertable = false, updatable = false) // 읽기전용 설정
  private Team team;

  @OneToOne
  @JoinColumn(name="LOCKER_ID")
  private Locker locker;

  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Locker getLocker() {
    return locker;
  }

  public void setLocker(Locker locker) {
    this.locker = locker;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
