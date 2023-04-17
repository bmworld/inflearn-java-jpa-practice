package jpabook.jpashop.domain.item;


import jpabook.jpashop.controller.BookForm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends Item {

    private String author;
    private String isbn;

    public void createItem(BookForm form) {
        super.setName(form.getName());
        super.setPrice(form.getPrice());
        super.setStockQuantity(form.getStockQuantity());
        this.author = form.getAuthor();
        this.isbn = form.getIsbn();
    }

    public void updateItem(BookForm form) {
        super.setId(form.getId());
        super.setName(form.getName());
        super.setPrice(form.getPrice());
        super.setStockQuantity(form.getStockQuantity());
        this.author = form.getAuthor();
        this.isbn = form.getIsbn();
    }

}
