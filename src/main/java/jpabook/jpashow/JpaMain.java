package jpabook.jpashow;

import jpabook.jpashow.domain.Member;
import jpabook.jpashow.domain.Team;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;

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
 */
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello");//지정된 경로에 있는 persisten.xml의 name 속성

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

/*
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeamId(team.getId());
            em.persist(member);


            Member findMember = em.find(Member.class, member.getId());
            Long findTeamId = findMember.getTeamId();
            Team findTeam = em.find(Team.class, findTeamId);
*/
            //단방향 매핑
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId()); //영속성 context의 1차 캐쉬에서 호출
            System.out.println("findTeam = " + findMember.getTeam().getName());

            //수정 예시
            Team newTeam = em.find(Team.class, 2L); // 2L 팀이 있다고 가정하고
            findMember.setTeam(newTeam); // setter로 바꾼 후 commit하면 dirty check 해서 update 날라감

            tx.commit();
        }catch(Exception ex){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
