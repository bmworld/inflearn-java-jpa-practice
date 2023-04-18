package jpaWithApi.jpashop.repository;


import jpaWithApi.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

  private final EntityManager em;

  public void save(Item item) {
    // Entity에 저장하기 전 까지는 item은 id 값이 없다.
    // 따라서, 영속성 Context에 등록되지 않은 경우는 getId 값이 Null이며,
    // 그에 따라서 영속성 context에 신규등록과정이 필요하다.
    if (item.getId() == null) {
      em.persist(item);
    } else{
      em.merge(item);
    }
  }

  public Item findOne(Long id) {
    return em.find(Item.class,id);
  }

  public List<Item> findAll() {
    return em.createQuery("select i from Item i", Item.class)
        .getResultList();
  }

}
