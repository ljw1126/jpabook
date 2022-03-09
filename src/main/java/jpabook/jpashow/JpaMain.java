package jpabook.jpashow;

import jpabook.jpashow.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * ※ spring boot , jpa 사용시 속성명을 낙타표기법으로 잡으면, jpa는 '_'형태로 바꿔줌
 *    (회사, 팀 스타일에 맞춰 속성명 정하기! 낙타표기법이든 '_' 든 )
 */
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello");//지정된 경로에 있는 persisten.xml의 name 속성

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //맴버 저장
            Member member1 = new Member();
            member1.setName("A");

            Member member2 = new Member();
            member2.setName("B");

            Member member3 = new Member();
            member3.setName("C");

            System.out.println("==================");

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            tx.commit();
        }catch(Exception ex){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
