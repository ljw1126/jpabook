package jpabook.jpashow.domain;

import javax.persistence.*;
import java.util.Objects;

@Deprecated
@Entity
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name="DELIVERY_ID")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Column
    private DeliveryStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(getId(), delivery.getId()) && Objects.equals(getOrder(), delivery.getOrder()) && Objects.equals(getAddress(), delivery.getAddress()) && getStatus() == delivery.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOrder(), getAddress(), getStatus());
    }
}
