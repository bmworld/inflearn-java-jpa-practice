package study.datajpa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member {
    @Id @GeneratedValue
    private Long id;

    private String name;

    protected Member() {
        // JPA 표준 스펙 중, Entity는 기본 생성자 필요함.
        // JPA Proxy 사용 시, private 접근자일 경우 불가하므로, protected로 설정한다.
    }

    public Member(String name) {
        this.name = name;
    }
}
