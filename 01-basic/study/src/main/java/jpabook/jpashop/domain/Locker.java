package jpabook.jpashop.domain;


import jpabook.jpashop.domain.member.Member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Locker {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "locker") // mappedBy의 값은 반대쪽에 자신이 매핑되어 있는 `필드명`
    private Member member;

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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
