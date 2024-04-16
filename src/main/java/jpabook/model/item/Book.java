package jpabook.model.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("B") // dtype = "M"
public class Book extends Item {
    private String author;
    private String isbn;

    public Book() {
    }

    public Book(Long id, Long price, Long stockQuantity, String author, String isbn) {
        super(id, price, stockQuantity);
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
