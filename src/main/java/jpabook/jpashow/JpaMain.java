package jpabook.jpashow;


import jpabook.jpashow.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;

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

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("city1", "street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족팔");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1","street","zipcode")); // 값타입이 엔티티로 승급한다고 표현함
            member.getAddressHistory().add(new AddressEntity("old2","street","zipcode"));

            em.persist(member);

            em.flush();
            em.clear();
            //DB에 저장되어 있고, 영속성 컨텍스트는 비어있는 상태
            Member findMember = em.find(Member.class, member.getId()); // select MEMBER 만 나감( 값 타입 컬렉션은 지연로딩)

           /* List<AddressEntity> addressHistory = findMember.getAddressHistory();
            for (AddressEntity address : addressHistory) {
                System.out.println("address = " + address.getCity()); // 지연로딩
            }

            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood); // 지연로딩
            }

            //수정 homeCity -> newCity
            //findMember.getHomeAddress().setCity("newCity"); // 값 타입은 immutable 해야 해서 private 하거나 setter 생성 x(side effect 발생가능) .. 이유 다시 공부하기

            //(중요)값타입은 인스턴스 새로 만들어서 완전히 갈아 끼워야함 !!
            Address address = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", address.getStreet(), address.getZipcode())); // update 문 나감

            //치킨 -> 한식 , 값타입이니 통으로 갈아끼워야 함
            findMember.getFavoriteFoods().remove("치킨"); // delete 문 나감
            findMember.getFavoriteFoods().add("한식"); // insert 문 나감, MEMBER 소속의 값이고 생명주기 과

            // equals() 와 hashCode()구현 꼭 해주기 ! -> 컬렉션에서 해당 메소드로 객체찾음
            findMember.getAddressHistory().remove(new AddressEntity("old1","street","zipcode")); // delete 문 실행
            findMember.getAddressHistory().add(new AddressEntity("newCity","street","zipcode")); // insert 문 실행, 근데 old2가 살아있어서 old2, newCity insert 문 총 2개 실행됨!!
*/
            tx.commit();
        }catch(Exception ex){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
