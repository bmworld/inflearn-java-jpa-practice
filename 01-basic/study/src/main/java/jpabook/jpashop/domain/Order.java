package jpabook.jpashop.domain;


import jpabook.jpashop.domain.embeddedType.Address;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "ORDERS")
public class Order extends BaseEntity{

  @Id
  @GeneratedValue
  @Column(name = "ORDER_ID")
  private Long id;
  private int orderAmount;
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name="MEMBER_ID")
  private Member member;

  @OneToOne(fetch = LAZY, cascade = ALL)
  @JoinColumn(name="DELIVERY_ID")
  private Delivery delivery;

  @OneToMany(mappedBy = "order", cascade = ALL) // 양방향 바인딩하고, 연관관계의 주인은, OrderItem에 있는 order이므로, 해당 주인을 설정해준다.
  private List<OrderItem> orderItems = new ArrayList<>();



  @ManyToOne
  @JoinColumn(name="PRODUCT_ID")
  private Product product;

  private LocalDateTime orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Embedded
  private Address address;


  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Member getMember() {
    return member;
  }

  public void setMember(Member member) {
    this.member = member;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Delivery getDelivery() {
    return delivery;
  }

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }


  public int getOrderAmount() {
    return orderAmount;
  }

  public void setOrderAmount(int orderAmount) {
    this.orderAmount = orderAmount;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }


}
