package jpabook.model.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("M") // dtype = "M"
public class Movie extends Item {
    private String director;
    private String actor;

    public Movie() {
    }

    public Movie(Long id, Long price, Long stockQuantity, String director, String actor) {
        super(id, price, stockQuantity);
        this.director = director;
        this.actor = actor;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
