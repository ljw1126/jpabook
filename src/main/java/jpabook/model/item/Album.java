package jpabook.model.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A") // dtype = "M"
public class Album extends Item {
    private String artist;
    private String etc;

    public Album() {
    }

    public Album(Long id, Long price, Long stockQuantity, String artist, String etc) {
        super(id, price, stockQuantity);
        this.artist = artist;
        this.etc = etc;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }
}
