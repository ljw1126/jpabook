package jpabook.repository.simplequery;

import jakarta.persistence.EntityManager;
import jpabook.repository.OrderSearch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public OrderSimpleQueryRepository(EntityManager em) {
        this.em = em;
    }

    public List<SimpleOrderQueryDto> findOrderDtos(OrderSearch orderSearch) {
        List<SimpleOrderQueryDto> result = em.createQuery(
                        " select new jpabook.repository.simplequery.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o " +
                                " join fetch o.member m " +
                                " join fetch o.delivery d ", SimpleOrderQueryDto.class)
                .getResultList();

        return result;
    }
}
