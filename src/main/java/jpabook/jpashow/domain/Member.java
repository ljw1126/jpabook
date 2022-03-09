package jpabook.jpashow.domain;

import javax.persistence.*;

//Setter 는 아무데서나 남발 하면 유지보수성 나빠지므로 지양
@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name ="USERNAME") // 이런 anotation은 db와 매핑하는 용도
    private String username;

    /*@Column(name="TEAM_ID")
    private Long teamId;*/

    @ManyToOne
    @JoinColumn(name="TEAM_ID")
    private Team team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}