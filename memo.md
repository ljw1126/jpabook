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

## 일대일(1:1) 관계 
- **일대일** 관계는 그 반대도 **일대일**
- 주 테이블이나 대상 테이블 중에 외래 키 선택 가능 
  - 주 테이블에 외래 키 
  - 대상 테이블에 외래 키 
- 외래 키에 데이터베이스 유니크 제약조건 추가

#### 주 테이블에 외래키 양방향 
- Member , Locker 테이블있을때 Fk가 Member에 있는 경우 
- 다대일 양방향 매핑 처럼 **외래 키가 있는 곳이 연관관계의 주인** 
- 반대편은 mappedBy 적용

```java
import javax.persistence.OneToOne;

@Entity
public class Member {
  ..
  @OneToOne
  @JoinColumn(name = "LOCKER_ID")
  private Locker locker;
  ..
}

@Entity
public class Locker {
  ..
  @OneToOne(mappedBy = "locker")
  private Member member; // 일대일 양방향 , 읽기 전용
  ..
}

```

#### 일대일 : 대상 테이블에 외래 키 단방향 
- 외래키가 Member가 아닌 Locker에 MEMBER_ID 있는 경우 
  - 이런 단방향 관계는 JPA 지원 x 
  - 단 양방향 관계는 지원
    - Member에 Locker (FK) 두고 주인으로 하든, Locker에 Member(FK) 두고 주인으로 하든 
    - 나중에 확장하는데(ex. 1:N , N: 1) 고려하는 요인이 될 수 있음 

#### 일대일 정리  
- 주 테이블에 외래키 설정하는 경우 
  - 주 객체가 대상 객체의 참조를 가지는 것 처럼, 주 테이블에 외래 키를 두고 대상 테이블을 찾음 
  - 객체 지향 개발자 선호 
  - JPA 매핑 관리 
  - 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능 
  - 단점 : 값이 없으면 외래 키에 null 허용 
- 대상 테이블에 외래 키 
  - 대상 테이블에 외래 키가 존재 
  - 전통적인 DB 개발자 선호 
  - 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지 
  - 단점 : 프록시 기능의 한계로 **지연 로딩으로 설정해도 항상 즉시 로딩됨**(뒤에 설명)

> 🤔 음.. 주 테이블에 FK 있으면 확인 후 지연로딩 가능한데, 양방향 일대일 잡으면 JPA가 FK를 가지고 Locker를 바로 즉시 로딩해버린다는 말인데 ..

## 다대다(N:M)  관계
- 결론적으로 실무에서 쓰면 x 
- 관계형 DB는 정규화된 테이블 2개로 다대다 관계 표현 x 
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야 함
  - ex. Member와 Product 테이블 있을때 중간 테이블로 다대다 풀어야 함 
- 객체는 컬렉션을 사용해서 객체 2개로 다대다 관계 가능 
- **@ManyToMany** 사용
- **@JoinTable** 로 연결 테이블 지정
- 다대다 매핑 : 단방향, 양방향 가능 

```java 
@Entity
public class Member{
  ..
  @ManyToMany
  @JoinTable(name="MEMBER_PRODUCT")  // MEMBER_PRODUCT 이름의 중간 테이블 생성됨
  private List<Product> products = new ArrayList<>();
  ..
}

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy="products")
    private List<Member> members = new ArrayList<>();
    
}

```

#### 다대다 매핑의 한계 
- 💩**편리해 보이지만 실무에서 사용 x** 
- 연결 테이블이 단순히 연결만 하고 끝나지 않음 
- 주문시간, 수량 같은 데이터가 들어 올 수 있음 → 중간 테이블에 컬럼 추가 x 💩 

#### 다대다 한계 극복 
- **연결 테이블용 엔티티 추가(연결 테이블을 엔티티로 승격)**
- @ManyToMany💣 → **@OneToMany, @ManyToOne** 

```java
@Entity
public class Member{
  ..
  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts = new ArrayList<>();
  ..
}

@Entity
public class MemberProduct { // (권장)중간 테이블이 엔티티로 승격!😎✨

    @Id
    @GeneratedValue
    private Long id;  // 의미없는 값을 사용하길 권장(유연성)

    //아니면 MEMBER_ID , PRODUCT_ID 두개 묵어서 PK,FK로 쓸는 것도 가능하나 경험 의거해 비추함 
  
    @ManyToOne
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;

    // 추가 필드 
    private int count;
    private int price;
    private LocalDateTime oderDateTime;
}



@Entity
public class Product {
  ..
  @OneToMany(mappedBy = "product")
  private List<MemberProduct> memberProducts = new ArrayList<>();
  ..
}

```

## 실전예제3 - 다양한 연관관계 매핑 
- 배송 , 카테고리 추가 // ERD 참고  