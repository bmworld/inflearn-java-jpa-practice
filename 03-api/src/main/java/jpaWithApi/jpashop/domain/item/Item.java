package jpaWithApi.jpashop.domain.item;

import jpaWithApi.jpashop.domain.Category;
import jpaWithApi.jpashop.domain.order.OrderItem;
import jpaWithApi.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
  @Id
  @GeneratedValue
  @Column(name = "item_id")
  private Long id;

  private String name;

  private int price;

  private int stockQuantity;

  @ManyToMany(mappedBy = "items")
  private List<Category> categories = new ArrayList<>();

  @OneToMany(mappedBy = "item")
  private List<OrderItem> orderItems = new ArrayList<>();


  // ** 비즈니스 로직 **
  /**
   * 재고 증가
   */
  public void addStock(int quantity) {
    this.stockQuantity += quantity;
  }

  /**
   *  재고 감소
   */
  public void removeStock(int quantity) {
    int restStock = this.stockQuantity - quantity;

    if (restStock < 0) {
      throw new NotEnoughStockException("Need more stock");
    }
    this.stockQuantity -= quantity;

  }
}
