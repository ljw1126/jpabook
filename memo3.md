## 프록시와 연관관계 정리 

### 1. 프록시 
#### 프록시 기초 
- em.find() vs em.getReference()
  - em.find() : 호출시 바로 쿼리 나감 , DB 통해 실제 엔티티 객체 조회
  - em.getReference() : 객체값 사용시 쿼리 나감, DB 조회를 미루는 가짜(프록시) 엔티티 객체 조회
    - hibernate가 proxy Entity 를 넘겨줌 
#### 프록시 특징 (1) 
- 실제 클래스를 상속 받아서 만들어짐.(hibernate 내부 처리해 줌) 
  - 프록시 객체는 실제 객체의 참조(target)를 보관 ( default null )
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상)

#### 프록시 특징 (2)
- 프록시 객체는 처음 사용할 때 한번만 초기화 
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님
  - 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능 
- 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야함 
  - **== 비교 실패, 대신 instance of 사용(JPA에서 proxy 사용시 이걸 기본으로 생각할 것, 근데 잘 쓸일 없다함)** 
- (심화)영속성 컨텍스트에 찾는 엔티티가 이미 있으면 **em.getReference()를 호출해도 실제 엔티티 반환**
  - (이유)이미 조회된 상태에서 proxy 구분의미 없음
  - (중요)한 트랜잭션 안에서 두번 조회시 == 무조건 true가 보장됨 => 그렇기 때문에 두번 조회시 proxy 구분 의미 없음 //얘사 첨ㄱㅎ
- (심화)영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시 초기화 문제 발생 
  - em.detach();/em.close();/em.clear(); 만나고 객체를 호출하면
  - hibernate는 org.hibernate.LazyInitializationException 예외를 터뜨림 // **실무에서 많이 발생한다함**
  - em.getReference() * 2 조회시 hibernate proxy 동일함 
  - em.getReference() 호출 후 em.find() 호출하면 둘다 hibernate proxy 반환됨
  - (중요) proxy가 아니든 개발하는게 중요.. 한 트랜잭션 두번 조회시 jpa 는 동일하게 맞춰줌! 

#### 프록시 객체의 초기화(영상 참고 15분)
- em.getReference()로 조회시 hibernate 내부에서 해당 Entity proxy 객체 생성
  - @Entity 타입의 target 객체 초기화 (default null)
- 그리고 실제 객체의 값을 호출할대 영속성 컨텍스트에게 요청해서 DB 조회 후 캐시에 Entity 넣고 proxy 의 target은 조회한 객체를 가르키게 됨

> 이런 매커니즘으로 동작한다고 이해하면 됨, em.getReference()는 실무에서 안씀. 뒤에 나올 즉시/지연 로딩 설명하기 위해 앞서 설명함

#### 프록시 확인 
- 프록시 인스턴스의 초기화 여부 확인 
> em.getPersistenceUnitUtil.isLoaded(Object entity) // return boolean
- 프록시 클래스 확인 방법
> entity.getClass().getName() 출력(..javasist.. or HibernateProxy...)
- 프록시 강제 초기화 
> org.hibernate.Hibernate.initialize(Object entity);
- 참고 : JPA 표준은 강제 초기화 없음.
---

### 2. 즉시로딩과 지연로딩 
#### 지연로딩 LAZY를 사용해서 프록시로 조회 
- Member 와 Team을 따로 사용하는 경우
```java 
@Entity 
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  
  @Column(name = "USERNAME")
  private String name;
  
  @ManyToOne(fetch = FetchType.LAZY) //** 지연로딩 우선 프록시 엔티티 조회 후 실제 team의 메소드 호출시 컨텍스트 통해 DB 초기화 !  
  @JoinColumn(name = "TEAM_ID")
  private Team team;
}
```
#### 즉시로딩 EAGER로 조회 
- Member 와 Team 을 즉시 사용하는 경우 
- JPA 구현체는 가능하면 join 을 사용해서 SQL 한번에 함께 조회
```java
@Entity 
public class Member {
  
  @Id @GeneratedValue
  private Long id;
  
  @Column(name = "USERNAME")
  private String name;
  
  @ManyToOne(fetch = FetchType.EAGER) //** 즉시로딩, 프록시 x    
  @JoinColumn(name = "TEAM_ID")
  private Team team;
}
```

#### (중요!) 프록시와 즉시로딩 주의
- **가급적 지연 로딩만 사용 (특히 실무에서!)**
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생 
  - join 이 5개면 query가 그만큼 나감 ( 2~3개면 괜찮 )  
- **즉시 로딩은 JPQL 에서 N + 1 (=> 1+N , 최적화 1개에 부수적인 N개가 발생) 문제를 일으킨다.** 
  - jpql 사용해서 select * from Member 해서 query 1번 나감 
  - 그런데 Member에 Team이 EAGER로 되어 있으면 JPA에서 이걸 보고 Team 조회 query 실행(select * from team where team_id = xx) 
  - 이것이 N+1
- **@ManyToOne, @OneToOne 은 기본이 즉시 로딩 -> LAZY로 설정할 것 !** 
  - 그래도 Team 사용시 뒤늦게 query가 나감.. 
  - 그래서 **FETCH JOIN** 활용해서 한방에 조회해서 해결하는 방법 있음 // 그외 다른 방법도 있다 함 
    - 값이 다 채워져 있으니 loop 돌려도 더이상 query 안나감 
- @OneToMany, @ManyToMany 는 기본이 지연 로딩

#### 지연 로딩 활용   // 실무에서는 지연로딩으로 다 발라야 함!😀 
- Member와 Team 은 자주 함께 사용 -> **즉시로딩**
- Member와 Order는 가끔 사용 -> **지연로딩**
- Order와 Product는 자주 함께 사용 -> **즉시 로딩**

#### 지연 로딩 활용 - 실무 
- **모든 연관관계에 지연 로딩을 사용하라!** 
- **실무에서 즉시 로딩을 사용하지 마라 !** 
- JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라! (뒤에 설명)
- 즉시 로딩은 상상하지 못한 쿼리가 나간다. 

### 3. 영속성 전이(CASCADE)와 고아객체
- 즉시로딩/지연로딩 상관 x 
- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을때 사용 
  - 예) 부모 엔티티를 저장시 자식 엔티티로 함께 저장 
```java 
@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Child> childList = new ArrayList<>();
  
    ..getter/setter   
}    

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
    ..getter/setter
}

//JpaMain에서 
try {
      Child child1 = new Child();
      Child child2 = new Child();

      Parent parent = new Parent();
      parent.addChild(child1);
      parent.addChild(child2);

      em.persist(parent); // 2. cascade = CascadeType.ALL 옵션 추가후 Collection에 있는 것도 한번 persist 해줄 수 있게 됨
      /*  1. cascade = CascadeType.ALL 옵션 추가 전에는 child1,2도 persist 해야 했지만
      em.persist(child1);
      em.persist(child2);
      */
      tx.commit();
}
```

#### 영속성 전이 : CASCADE 주의 
- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음 
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화 하는 편리함을 제공할 뿐
  - ex. 게시판 - 게시글 첨부파일 같은 경우 의미 있음 (**단일 소유자일 때, 단일 엔티티 종속적일때, 라이프 사이클 동일할때**) 
  - 단, 파일을 다른 엔티티에서 관리하면 사용 x 
#### CASCADE 종류 (아래 3종류만 실무에서 많이 씀)
- **ALL : 모두 적용** 
- **PERSIST : 영속** 
- **REMOVE : 삭제** 
- MERGE : 병합
- REFRESH : REFRESH
- DETACH : DETACH

#### 고아 객체 
- 고아 객체 제거 : 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제 
- 옵션 → **orphanRemoval = true** 
  - ex. Parent 에서 관리하는 Child Collection 에 대해 *.remove(idx) 동작시.. 실제 delete query 실행됨 
- 주의 
  - 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능 
  - **참조하는 곳이 하나일때 사용해야 함!**
  - **특정 엔티티가 개인 소율할 때 사용해야 함(중요)**
  - @OneToOne, @OneToMany 만 가능 
  - 참고 : 개념적으로 부모를 제거하면 자식은 고아가 된다.
    - 따라서 고아 객체 제거 기능을 활성화하면, **부모를 제거할 때 자식도 함께 제거**된다. 
    - 이것은 **CascadeType.REMOVE** 처럼 동작한다.
```java 
@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", orphanRemoval = true) // 옵션 추가
    private List<Child> childList = new ArrayList<>();
    
    ...
}

//JpaMain 내용 중 
 try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0); // delete query 실행됨

            tx.commit();
 }
```

#### 영속성 전이 + 고아 객체, 생명주기 
- **CascadeType.ALL + orphanRemovel = true**
- 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove로 제거
- 두 옵션을 모두 활성화 하면 부모 엔티티를 통해저 자식의 생명 주기를 관리할 수 있음 
- 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용 //이런게 있다 정도 ..

```java 
@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true) // 옵션 추가
    private List<Child> childList = new ArrayList<>();
    
    ...
}

//JpaMain 내용 중 
 try {

            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent); // 부모 객체만 persist 했는데 자식 객체도 함께 처리됨 

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            //em.remove(findParent); 또는 아래와 같이 child 생명주기 관리 가능
            findParent.getChildList().remove(0); // delete query 실행됨

            tx.commit();
 }
```