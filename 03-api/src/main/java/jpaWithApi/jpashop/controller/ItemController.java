package jpaWithApi.jpashop.controller;


import jpaWithApi.jpashop.DTO.UpdateBookDto;
import jpaWithApi.jpashop.domain.item.Book;
import jpaWithApi.jpashop.domain.item.Item;
import jpaWithApi.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController{

  private final ItemService itemService;

  @GetMapping("/items")
  public String list(Model model) {
    List<Item> items = itemService.findItems();
    model.addAttribute("items", items);
    return "items/itemList";
  }

  @GetMapping("/items/new")
  public String createForm(Model model) {
    model.addAttribute("form", new BookForm());
    return "items/createItemForm";
  }

  @PostMapping("/items/new")
  public String create(BookForm form) {
    Book book = new Book();
    /**
     * 김영한 강사님: 실무에서는 setter 사용하지 않음
     */
    book.createItem(form);
    itemService.saveItem(book);

    return "redirect:/";
  }


  @GetMapping("/items/{itemId}/edit")
  public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
    Book item = (Book) itemService.findOne(itemId);
    BookForm form = new BookForm();
    form.setId(item.getId()); // 얘는 DB에 PK값이 있는 준영속 상태다.
    form.setName(item.getName());
    form.setPrice(item.getPrice());
    form.setStockQuantity(item.getStockQuantity());
    form.setAuthor(item.getAuthor());
    form.setIsbn(item.getIsbn());

    model.addAttribute("form", form);

    return "items/updateItemForm";
  }


  @PostMapping("/items/{itemId}/edit")
  public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
    // ! 사용금지: merge를 사용하는 방법 (merge 사용 시, 특정필드 값이 없을 경우 null값이 적용될 수 있음)
//    Book book = new Book();
//    book.updateItem(form);
//
    UpdateBookDto dto = UpdateBookDto.builder()
        .id(itemId)
        .price(form.getPrice())
        .name(form.getName())
        .stockQuantity(form.getStockQuantity())
        .author(form.getAuthor())
        .isbn(form.getIsbn())
        .build();
    itemService.updateBook(itemId, dto );


    return "redirect:/items";
  }

}
