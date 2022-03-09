package jpabook.jpashow.domain;

import javax.persistence.*;

//Setter 는 아무데서나 남발 하면 유지보수성 나빠지므로 지양
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID") //대소문자는 회사 룰 따르기
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    public Member(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
