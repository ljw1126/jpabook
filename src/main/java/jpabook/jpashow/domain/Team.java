package jpabook.jpashow.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private  Long id;

    private String name;
    /**
     * 연관관계의 주인과 mappedBy
     * - 객체와 테이블 간의 연관관계를 맺는 차이를 알아야함
     *   - 객체 연관관계 = 2개
     *      1) 회원 → 팀 연관관계 1개(단방향)
     *      2) 팀 → 회원 연관관계 1개(단방향)
     *   - 테이블(db입장) 연관관계 = 1개
     *      1) 회원 ↔ 팀 연관관계 1개 (양방향) // FK 하나로 모든 연관관계가 끝남
     * - '객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개다'
     *   - 객체를 양방향으로 참조하려면 '단방향 연관관계를 2개*' 만들어야 한다.
     * - 테이블은 '외래 키 하나'로 두 테이블의 연관관계를 관리 ex. MEMBER.TEAM_ID 하나로 양방향 관계 가짐
     *
     * 연관관계의 주인(Owner) - 양방향 관계에서 사용
     * - 양방향 매핑 규칙
     *   - 객체의 두 관계중 하나를 연관 관계의 주인으로 지정
     *   - '연관관계의 주인만이 왜리 키를 관리(등록,수정)'
     *   - 주인이 아닌쪽은 읽기만 가능
     *   - 주인은 mappedBy 속성 사용 x (진짜 매핑 - Member.team / 다 N)
     *   - 주인이 아니면 mappedBy 속성으로 주인 지정 (가짜 매핑 - Team.members / 일 1)
     *   - (기준)여기서는 Member.team(다) 이 주인! 외래키가 있는 곳을 주인으로 정해라 !!
     *     반대로 하게 되면 성능이슈 있을 수 있어 나중 설명 ..
     */
    @OneToMany(mappedBy = "team")      // Member class에 team에 mapping 되어 있다는 뜻
    private List<Member> members = new ArrayList<>(); // null point 안뜨게 하는 관례

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }
}
