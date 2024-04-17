package jpabook.api;

import jpabook.model.Address;
import jpabook.model.Order;
import jpabook.model.OrderStatus;
import jpabook.repository.OrderRepository;
import jpabook.repository.OrderSearch;
import jpabook.repository.simplequery.OrderSimpleQueryRepository;
import jpabook.repository.simplequery.SimpleOrderQueryDto;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    public OrderSimpleApiController(OrderRepository orderRepository, OrderSimpleQueryRepository orderSimpleQueryRepository) {
        this.orderRepository = orderRepository;
        this.orderSimpleQueryRepository = orderSimpleQueryRepository;
    }

    /**
     * Bad Case
     * 문제1. Order -> Member -> Order 순환참조
     * 문제2. jackson 변환시 Lazy 로딩 이슈
     * <p>
     * API 스펙과 엔티티가 강결합
     * 엔티티 스펙 노출로 인해 유지보수 어려워짐
     * 지연 로딩에 대한 쿼리 성능 낭비
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllBy(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * 개선. 엔티티 -> DTO 변환
     * 문제. N + 1 발생
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAllBy(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    /**
     * 개선. fetch join 적용 -> 쿼리 1번 실행
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        return orderRepository.findAllWithMemberDelivery(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    /**
     * 개선. JPA에서 DTO로 변환 -> 내가 원하는 필드만 지정
     */
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos(new OrderSearch());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // LAZY 초기화
            this.orderDate = order.getOrderDate();
            this.status = order.getStatus();
            this.address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
