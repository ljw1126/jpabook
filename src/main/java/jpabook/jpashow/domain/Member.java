package jpabook.jpashow.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Embedded
    private Period workPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                    column=@Column(name="WORK_CITY")),
            @AttributeOverride(name="street",
                    column=@Column(name="WORK_STREET")),
            @AttributeOverride(name="zipcode",
                    column=@Column(name="WORK_ZIPCODE"))
    })
    private Address workAddress;

    @Embedded
    private Address homeAddress;


    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
        @JoinColumn(name="MEMBER_ID")
    ) // FAVORITE_FOOD 테이블에 MEMBER_ID FK 생성 (join 용)
    @Column(name = "FOOD_NAME") // 예외적으로 컬럼명 지정해줌
    private Set<String> favoriteFoods = new HashSet<>();

/*    @OrderColumn(name = "address_history_order")
    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns =
        @JoinColumn(name = "MEMBER_ID")
    ) // ADDRESS 테이블에 필드명 동일, MEMBER_ID FK 추가된 형태로 테이블 생성
    private List<Address> addressHistory = new ArrayList<>();*/

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

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

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

  /*  public List<Address> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<Address> addressHistory) {
        this.addressHistory = addressHistory;
    }*/

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) && Objects.equals(username, member.username) && Objects.equals(workPeriod, member.workPeriod) && Objects.equals(workAddress, member.workAddress) && Objects.equals(homeAddress, member.homeAddress) && Objects.equals(favoriteFoods, member.favoriteFoods) && Objects.equals(addressHistory, member.addressHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, workPeriod, workAddress, homeAddress, favoriteFoods, addressHistory);
    }
}
