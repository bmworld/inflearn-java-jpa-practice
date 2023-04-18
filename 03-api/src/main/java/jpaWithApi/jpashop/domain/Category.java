package jpaWithApi.jpashop.domain;

import jpaWithApi.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name="category_item",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="item_id")
    ) // 다대다 => 중간 테이블 필요 ( ** 실무에서는 사용하지 마시라)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;


    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


    // ** 연관관계 편의 메서드 **
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);

    }

}
