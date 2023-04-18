package jpaWithApi.jpashop.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateBookDto {

  private Long id;

  private int price;
  private String name;
  private int stockQuantity;

  private String author;
  private String isbn;

}
