package jpaWithApi.jpashop.service;

import jpaWithApi.jpashop.DTO.UpdateBookDto;
import jpaWithApi.jpashop.domain.item.Book;
import jpaWithApi.jpashop.domain.item.Item;
import jpaWithApi.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

  private final ItemRepository itemRepository;


  @Transactional
  public void saveItem(Item item) {
    itemRepository.save(item);
  }

  @Transactional
  public void updateBook(Long itemId, UpdateBookDto dto) {
    Book foundItem = (Book) itemRepository.findOne(itemId); // 얘는 영속상태다.
    foundItem.setPrice(dto.getPrice());
    foundItem.setName(dto.getName());
    foundItem.setStockQuantity(dto.getStockQuantity());
    foundItem.setAuthor(dto.getAuthor());
    foundItem.setIsbn(dto.getIsbn());
  }

  public List<Item> findItems() {
    return itemRepository.findAll();
  }


  public Item findOne(Long itemId) {
    return itemRepository.findOne(itemId);
  }

}
