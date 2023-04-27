package study.datajpa.domain;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)// JPA 표준 스펙 중, Entity는 기본 생성자 필요함. => Why? JPA Proxy 사용 시, private 접근자일 경우 불가하므로, protected로 설정한다.
@ToString(of = {"id", "name", "age"}) // Entity 연관관계 필드는 무한 루프 방지를 위해, 제외하시라.
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    private int age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String name) {
        this.name = name;
    }

    public Member(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        changeTeam(team);
    }

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
