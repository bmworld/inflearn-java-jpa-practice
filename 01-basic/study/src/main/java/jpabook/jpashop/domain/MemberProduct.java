package jpabook.jpashop.domain;


import jpabook.jpashop.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_product")
public class MemberProduct {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;


    private int count;
    private int price;

    private LocalDateTime orderDateTime;


}
