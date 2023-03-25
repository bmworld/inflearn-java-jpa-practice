package hellojpa;



import com.sun.istack.NotNull;
import hellojpa.member.RoleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {})
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="name", nullable = false) // DB column 이름 지정
  private String username;

  private Integer age;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "VARCHAR(50) default 'USER'")
  private RoleType roleType;

  @Lob
  private String description;

  private LocalDate localDate;
  private LocalDateTime localDateTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;


  @Transient // DB에 추가하지 않고, Memory에서만 사용
  private String onlyMemoryField;

  protected Member() {}

  public Member(String username, int age, RoleType roleType, String description) {
    this.username = username;
    this.age = age;
    this.roleType = roleType;
    this.description = description;
  }

  public static Member of(String name) {
    return Member.of( name, 0, null, null);
  }


  public static Member of(String name, int age, RoleType roleType, String description) {
    return new Member(name, age, roleType, description);
  }


}
