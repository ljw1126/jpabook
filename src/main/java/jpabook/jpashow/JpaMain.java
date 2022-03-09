package jpabook.jpashow;

import jpabook.jpashow.domain.Member;
import jpabook.jpashow.domain.Team;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

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
            // 양방향 매핑시 가장 많이 하는 실수 - 연관관계의 주인에 값을 입력하지 않음
            Member member = new Member();
            member.setUsername("member1");
            em.persist(member); // Member에 team_id에 null 들어감

            Team team = new Team();
            team.setName("TeamA");
            //team.getMembers().add(member); // 얘는 읽기 전용.. jpa에서 저장시 사용안함*
            em.persist(team);

            team.addMember(member); // 둘중 하나 편한데로 메서드 생성해서 하면됨. 무한 루프 발생 안되게끔..*

            // 정상
            Team teamB = new Team();
            teamB.setName("TeamB");
            em.persist(teamB);

            Member member2 = new Member();
            member2.setUsername("member2");
            //member2.changeTeam(teamB); // 주인에 값이 들어감 (정상)*
            em.persist(member2);

            em.flush();
            em.clear();

            /*
                case1.
                 그런데 객체 지향 관계에서 team.getMembers().add(member)에 값을 넣는게 맞는 경우도 존재
                em.flush(); em.clear(); 하지 않은 상태에서 team.getMembers() 호출시
                1차 캐쉬에 실제 값이 없으므로..
                case2.
                 테스트 케이스 사용시
                즉.
                 양방향 사용시 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자!
                 - 연관관계 편의 메소드를 생성하자 -> Member에서 setTeam 할때 추가, 그리고 setter대신 메소드명으로 바꿔서 부각시키자
                 - 양방향 매핑시에 무한 루프를 조심하자
                   -> ex. toString() , lombok 라이브러리
                   -> json 생성 라이브러리(Controller에 entity 반환 x 무한루프발생가능.., dto 로 변환해서 하는걸 추천)
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
