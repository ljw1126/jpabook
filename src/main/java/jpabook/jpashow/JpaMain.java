package jpabook.jpashow;


import jpabook.jpashow.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * ※ spring boot , jpa 사용시 속성명을 낙타표기법으로 잡으면, jpa는 '_'형태로 바꿔줌
 *    (회사, 팀 스타일에 맞춰 속성명 정하기! 낙타표기법이든 '_' 든 )
 *
 * ※ 객체를 테이블에 맞춰 데이터 중심으로 모델링 하면 협력관계를 만들 수 x
 *    - '테이블은 외래 키로 조인'을 사용해서 연관 테이블 찾음
 *    - '객체는 참조(reference)'를 사용해서 연관 객체 찾음
 *    - 테이블과 객체 사이에는 이런 큰 간격(패러다임 차)이 있다.
 *  ※ 정리
 *    - '단방향 매핑만으로도 이미 연관관계 매핑은 완료!'
 *    - 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색)기능이 추가된 것 뿐
 *    - JPQL에서 역방향으로 탐색할 일이 많음
 *    - 단방향 매핑을 잘하고 양방향은 필요할때 추가해도 됨 ! (테이블에 영향 주지 x)
 *    - 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안됨 -> 정 안되면 연관관계 편의 메소드 활용
 *    - '연관관계의 주인은 외래 키의 위치를 기준으로 정해야 함'
 *
 */
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello");//지정된 경로에 있는 persisten.xml의 name 속성

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 1. 공유 주소로 각각 insert 수행
            Address address = new Address("city","street","1000");

            Member member = new Member();
            member.setUsername("member1");
            member.setWorkAddress(address);
            member.setWorkPeriod(new Period());
            em.persist(member);

            // 값을 복사해서 사용하면 독립적으로 update 가능
            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setWorkAddress(copyAddress);
            member2.setWorkPeriod(new Period());
            em.persist(member2);

            member.getWorkAddress().setCity("newCity");


            tx.commit();
        }catch(Exception ex){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
