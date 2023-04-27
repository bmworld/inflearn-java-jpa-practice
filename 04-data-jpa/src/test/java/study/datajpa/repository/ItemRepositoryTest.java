package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.domain.Item;

@SpringBootTest
public class ItemRepositoryTest {
  @Autowired
  ItemRepository itemRepository;

  @DisplayName("saveTest")
  @Test
  public void saveTest() throws Exception{
    // Given
    Item item = new Item("A");
    itemRepository.save(item);
    // When

    // Then

  }

}
