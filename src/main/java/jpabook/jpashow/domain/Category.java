package jpabook.jpashow.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue
    @Column(name="CATEGORY_ID")
    private Long id;

    private String name;

    //연관관계 주인. JoinTable에서 중간테이블 생성
    @ManyToMany
    @JoinTable(name="CATEGORY_ITEM"
            , joinColumns = @JoinColumn(name="CATEGORY_ID")
            ,inverseJoinColumns = @JoinColumn(name="ITEM_ID"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Category parent; // 상위 카테고리 , self mapping/join

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>(); // 양방향으로 자식 카테고리
}
