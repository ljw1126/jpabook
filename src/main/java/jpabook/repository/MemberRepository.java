package jpabook.repository;

import jakarta.persistence.EntityManager;
import jpabook.model.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {
    private final EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Member member) {
        em.persist(member);
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
