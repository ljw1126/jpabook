package jpabook.model.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.model.Category;

import java.util.List;

@Entity
@DiscriminatorValue("B") // dtype = "M"
public class Book extends Item {
    private String author;
    private String isbn;

    public Book() {
    }

    public Book(String author, String isbn) {
        this.author = author;
        this.isbn = isbn;
    }

    public Book(Long id, String name, int price, int stockQuantity, List<Category> categories, String author, String isbn) {
        super(id, name, price, stockQuantity, categories);
        this.author = author;
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


}
