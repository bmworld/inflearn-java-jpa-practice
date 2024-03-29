package jpabook.jpashop.domain;

import javax.persistence.*;
import javax.xml.stream.XMLEventWriter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "TEAM_ID")
  private Long id;
  private String name;

  @OneToMany(mappedBy="team")
  private List<Member> members = new ArrayList<>();

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

  public List<Member> getMembers() {
    return members;
  }
}
