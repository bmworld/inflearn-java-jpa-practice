package jpabook.jpashop.domain;

import jpabook.jpashop.domain.embeddedType.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "MEMBER_ID")
  private Long id;
  private String name;


  @Enumerated(EnumType.STRING)
  private RoleType roleType;

  @Embedded
  private Address address;


  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  private int age;


  public Team getTeam() {
    return team;
  }

  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Long getId() {
    return id;
  }

  public RoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(RoleType roleType) {
    this.roleType = roleType;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
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

  public void setTeam(Team team) {
  }

  @Override
  public String toString() {
    return "Member{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address=" + address +
            ", orders=" + orders +
            ", age=" + age +
            '}';
  }
}
