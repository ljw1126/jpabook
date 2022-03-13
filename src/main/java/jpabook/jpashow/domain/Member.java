package jpabook.jpashow.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//Setter 는 아무데서나 남발 하면 유지보수성 나빠지므로 지양
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name ="USERNAME") // 이런 anotation은 db와 매핑하는 용도
    private String username;

    @OneToOne
    @JoinColumn(name="LOCKER_ID")
    private Locker locker;

/*
    @ManyToMany
    @JoinTable(name="MEMBER_PRODUCT")
    private List<Product> products = new ArrayList<>();
*/
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}