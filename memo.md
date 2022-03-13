## 다양한 연관관계 매핑 

- 연관관계 매핑시 고려사항 3가지 
  - 다중성 
    - N:1 → @ManyToOne
    - 1:N → @OneToMany 
    - 1:1 → @OneToOne
    - N:N → @ManyToMany, 실무에서 쓰면 안됨💣
  - 단방향, 양방향 
    - 테이블
      - 외래키 하나로 양쪽 조인 가능, 사실 방향이라는 개념이 없음 
    - 객체 
      - 참조용 필드가 있는 쪽으로만 참조 가능 
      - 한쪽만 참조하면 단방향 , 양쪽이 서로 (필드) 참조하면 양방향
      - 객체 입장에서 양방향은 사실 x , 단방향 2개 있는거를 풀어 설명
  - 연관관계의 주인 
    - 테이블은 '**외래 키 하나**'로 두 테이블이 연관관계를 맺음 
    - 객체 양방향 관계는 A→B , B→A 처럼 '**참조가 2군데**'인 것을 뜻함
    - 객체 양방향 관계는 참조가 2군데 있음, 둘중 테이블의 외래키를 관리할 곳을 지정해야 함 
    - 연관관계의 주인 : 외래 키를 관리하는 참조 
    - 주인의 반대편 : 외래 키에 영향을 주지 않음. 단순 조회용(읽기만)

#### 다대일(N:1) 단방향 
- Member(N) → Team(1) 
  - Member에 FK(TEAM_ID)가 있어야 함 
- 가장 많이 사용하는 연관관계 
  - '다대일'의 반대는 '일대다'
  - **다**대일 , 연관관계 주인은 **다** 이다.
  - 단방향이니 TEAM 쪽에 아무것도 추가 x
#### 다대일(N:1) 양방향 
- 외래 키가 있는 쪽이 연관관계의 주인 
- 양쪽을 서로 참조하도록 개발

```java 
@Entity
public class Member{    // 다대일의 연관관계 주인, FK가 있는 곳
  ..

  @ManyToOne
  @JoinColumn(name="TEAM_ID")     
  private Team team;           
  
  ..
}

@Entity
public class Team{ // 다대일 양방향 관계 설정, 조회/읽기용
  ..
  
  @OneToMany(mappedBy = "team")      // Member class에 team에 mapping 되어 있다는 뜻
  private List<Member> members = new ArrayList<>(); // null point 안뜨게 하는 관례
  
  ..
}
```

#### 일대다(1:N) 단방향
- **일**이 연관관계 주인이다
- TEAM → MEMBER 봤을때 
- update 쿼리가 한번 더 나가니 잘 사용하지 x → 실전에서 테이블 많으니 유지보수 💣
- 👨‍💻✨**그래서 다대일 관계 선호하고, 필요시 다대일 양방향 설정해서 사용하길 권장** 
- 정리 
  - 일대다 단방향은 **일(1)이 연관관계의 주인**
  - 테이블 일대다 관계는 항상 **다(N) 쪽에 외래 키가 있음**
  - 객체와 테이블의 차이 때문에 반대편 테이블의 외래 키를 관리하는 특이한 구조 
  - @JoinColumn✨ 을 꼭 사용해야 함. 그렇지 않으면 default 조인 테이블 방식(중간 테이블 추가) 사용함 → 마찬가지로 하나 더 추가되니 성능..  
  - (단점) 엔티티가 관리하는 외래 키가 다른 테이블에 있음 
  - (단점) 연관관계 관리를 위해 추가로 UPDATE SQL 실행 
  - 고로 일대다 단방향 매핑보다는 **다대일 양방향 매핑을 사용**하자

#### 일대다 양방향 
- 일대다 양방향 매핑은 공식적으로 존재 x 
- @JoinColumn(**insertable = false, updatable = false**)
- **읽기 전용 필드**를 사용해서 양방향 처럼 사용하는 방법 
- 👨‍💻✨**고로 다대일 양방향을 사용하자**

```java
import javax.persistence.JoinColumn;

@Entity
public class Team {
  ..

  @OneToMany
  @JoinColumn(name = "TEAM_ID")  //주석처리하면 TEAM_MEMBER 테이블 만들어버림
  private List<Member> members = new ArrayList<>();
  
  ..
}

  /**
   *  member insert 
   *  → team insert, 이때 List<member> members에 위에 member객체 add ! 
   *  → 해당 member의 team_id(FK) update query 실행됨 
   *  👨‍💻 성능상 1건 더 쿼리 보내는게 단점 ✨
   */
  public static void main() { 
  ...
    try {
      Member member = new Member();
      member.setUsername("member1");
      em.persist(member);

      Team team = new Team();
      team.setName("teamA");
      team.getMembers().add(member); // 해당 member의 team_id(fk)에 update 날림
      em.persist(team);

      tx.commit();
    }
  ...
  }

@Entity
public class Member { // 일대다 양방향(억지로 한다면)
  ..
  //name="TEAM_ID" 있으면 마치 연관관계 주인인것 처럼 되어서 꼬여버림 
  //insertable = false, updatable = false 그래서 읽기 전용이 됨
  @ManyToOne
  @JoinColumn(name="TEAM_ID",insertable = false, updatable = false)
  private Team team;
  
  ..
}


```


