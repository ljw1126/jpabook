## Tip 
- Generate equals() and hashCode() wizard 할 때 Use getters during code generation 옵션 체크하기 
  - proxy 로 엔티티 속성 접근해서 값 비교할때, 필드 자체 접근이 안되니 getter 로 호출하도록 하는게 좋다함 (기본으로 할 것! 어차피 옵션만 체크하면 되니)
  
## 값 타입 

### 1. 기본 값 

#### JPA의 데이터 타입 분류 
- 엔티티 타입 
  - @Entity로 정의하는 객체 
  - 데이터가 변해도 식별자로 지속해서 추적 가능 
  - 예) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능 
- 값 타입 
  - int, Integer, String 처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체 
  - 식별자가 없고 값만 있으므로 변경시 추적 불가 
  - 예) 숫자 100을 200으로 변경하면 완전히 다른값으로 대체

#### 값 타입 분류 
- 기본 값 타입 
  - 자바 기본 타입(int, double,String)
    - 생명주기를 엔티티의 의존 
      - 예) 회원을 삭제하면 이름, 나이 필드로 함께 삭제 
    - 값 타입은 공유하면 x 
      - 예) 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨 (side effect)
  - 래퍼 클래스(Integer, Long)
  - String 
- 임베디드 타입(embedded type, 복합 값 타입)
- 컬렉션 값 타입(collection value type)

#### ※ 자바의 기본 타입은 절대 공유 x 
- int, double 같은 기본 타입(primitive type)은 절대 공유 안됨 
- 기본 타입은 항상 값을 복사함 
- Integer같은 Wrapper class나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경 x

```java
public class ValueMain {
    public static void main(String[] args) {
            int a = 10;
            int b = a; // 복사가 되어 b에 들어감 
    
            a = 20;
            System.out.println("a = " + a); // 20
            System.out.println("b = " + b); // 10
    }
}
public class ValueMain {
    public static void main(String[] args) { // 밑에서 String, Integer 대표 불변객체라 했는데.. 예시가 좀.. 임의 객체로 생각할 것 ! 
        Integer a = 10;
        Integer b = a; // reference 주소값 가져감
        // 여기에 특정 함수로 a 값 변경하면 b도 동일하게 값 출력됨.
        System.out.println("a = " + a);
        System.out.println("b = " + b); // reference 가 넘어가서 같은 instance 공유함
    }
} 
```

### 2. 임베디드 타입(복합 값 타입) --- 중요👍
#### 임베디드 타입(embedded type)
- 새로운 값 타입을 직접 정의할 수 있음 
- JPA는 임베디드 타입이라 함 
- 주로 기본 값 타입을 모아서 만들어 복합 값 타입이라고도 함 
- int, String 과 같은 타입 -> 추적/변경 x 

#### 임베디드 타입 사용법
- @Embeddable : 값 타입을 정의하는 곳에 표시 
- @Embedded : 값 타입을 사용하는 곳에 표시 
- 기본 생성자 필수 

#### 임베디드 타입 장점 
- 재사용, 높은 응집도
- Period.isWork() 처럼 해당 값 타입만 사용하는 의미있는 메소드를 만들 수 있음 
- 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함
  - 엔티티의 생명주기 따라감 

#### 임베디드 타입과 테이블 매핑(영상참고 5:00)
- 임베디드 타입은 엔티티의 값일 뿐이다. 
- 임베디드 타입을 사용하기 전과 후에 **매핑하는 테이블은 같다.**
- 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능 
  - 클래스 모델링이 깔끔해지고, 재활용성 증가 ( 현업에서 그렇게 많이 쓰진 않지만, 쓰는 경우 있음)
- 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음 
```java 
// 1. 초기 생성
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    //Period 로 묶고 싶음
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    //주소로 공통 뽑아서 사용하고 싶음
    private String city;
    private String street;
    private String zipcode;
}

//2. Period, Address class 생성
//@Embedded , @Embeddable 어노테이션 추가 → 동일한 테이블 생성되면서, 객체 지향스러워짐 
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Embedded
    private Period workPeriod;
    @Embedded
    private Address workAddress;
}


@Embeddable
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    .. // 기본 생성자,getter, setter 생략 
}


@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
    .. // 기본 생성자,getter, setter 생략
}

// 3. insert 예시 
public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello");//지정된 경로에 있는 persisten.xml의 name 속성

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("hello");
            member.setWorkAddress(new Address("city","street","zipcode"));
            member.setWorkPeriod(new Period());

            em.persist(member);

            tx.commit();
        }catch(Exception ex){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
}
```

#### 임베디드 타입과 연관관계(영상 15:34)
- @Embeddable 선언한 클래스 안에 value를 추가하거나 @Entity 추가도 가능
  - 간단히 설명하고 넘어감

#### @AttributeOverride : 속성 재정의 
- 한 엔티티에서 같은 값 타입을 사용하면 ? 
- 컬럼 명이 중복됨 
- **@AttributeOverrides, @AttributeOverride** 를 사용해서 컬럼명 속성을 재정의 
```java 
// 주소가 home이랑 work 구분해야 할 때 (잘쓰진 않음, @Override 예제 확인시 ctrl + q )
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Embedded
    private Period workPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                    column=@Column(name="WORK_CITY")),
            @AttributeOverride(name="street",
                    column=@Column(name="WORK_STREET")),
            @AttributeOverride(name="zipcode",
                    column=@Column(name="WORK_ZIPCODE"))
    })
    private Address workAddress; // Address 필드가 override되서 테이블 컬럼 생성됨

    @Embedded
    private Address homeAddress; 
    
    ..// 생성자, getter, setter 생략 
    // 테이블 생성시 workPeriod - city, street, zipcode가 연결되고 
    // 추가 필드로 override되어서 - WORK_CITY, WORK_STREET, WORK_ZIPCODE가 MEMBER 테이블 컬럼 추가됨 (create 문 확인) 
```
#### 임베디드 타입과 null 
- 임베디드 타입의 값이 null 이면 매핑한 컬럼 값은 모두 null 
  - Entity 에서 필드값을 null로 선언하면 다 null로 들어감 

### 3. 값 타입과 불변 객체 
> 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다.
#### 값 타입 공유 참조
- 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함 (side effect 발생 가능)
  - 예제 그림 참고
```java 
          // 1. 공유 주소로 각각 insert 수행
          Address address = new Address("city","street","1000");

          Member member = new Member();
          member.setUsername("member1");
          member.setWorkAddress(address);
          member.setWorkPeriod(new Period());
          em.persist(member);

          Member member2 = new Member();
          member2.setUsername("member2");
          member2.setWorkAddress(address); // 동일한걸 쓰면 compile level에서 막을 방법이 없음..
          member2.setWorkPeriod(new Period());

          em.persist(member2);

          // 2. member1만 수행하고 싶은데, member2로 update됨 -> side effect , 찾기 힘듦 
          member.getWorkAddress().setCity("newCity"); 
```
> 동시 수정을 의도할 수 있지만 값타입(임베디드 타입)으로 하면 안되고 Address가 Entity 타입이여야 한다 함(why?)

#### 값 타입 복사 
- 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험 
- 대신 값(인스턴스)를 복사해서 사용 
  - address 를 사용하고 newAddress 를 만들어서 활용 
```java 
  // 1. 공유 주소로 각각 insert 수행
  Address address = new Address("city","street","1000");
  
  Member member = new Member();
  member.setUsername("member1");
  member.setWorkAddress(address);
  member.setWorkPeriod(new Period());
  em.persist(member);
  
  // 값을 복사해서 사용하면 독립적으로 update 가능
  Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
  
  Member member2 = new Member();
  member2.setUsername("member2");
  member2.setWorkAddress(copyAddress);
  member2.setWorkPeriod(new Period());
  em.persist(member2);
  
  member.getWorkAddress().setCity("newCity"); // member만 "newCity" update됨 
  
```
#### 객체 타입의 한계 
- 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다(side effect 회피)
- 문제는 임베디드 타입처럼 **직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입**이다.
- 자바 기본 타입에 값을 대입하면 값을 복사한다. 
- **객체 타입은 참조 값(reference, 주소값)을 직접 대입하는 것을 막을 방법이 없다.**
- **객체의 공유 참조는 피할 수 없다.( '=' 만있으면 다 넣을 수 있다.)**

```java 
#기본 타입(primitive type) 
int a = 10;
int b = a; // 기본 타입은 값을 복사 
b = 4;   // 안전함 

#객체타입 
Address a = new Address("old");
Address b = a; // 객체 타입은 참조(reference, 주소값)를 전달
b.setCity("new");   // 안전하지 못함.. 주소값을 가지고 있어서 한 instance 갱신되니 둘다 갱신 
```

#### 불변 객체 
- 객체 타입을 수정할 수 없게 만들면 **부작용을 원천 차단**
- **값 타입은 불변 객체(immutable object)로 설계해야함**
- **불변 객체 : 생성 시점 이후 절대 값을 변경할 수 없는 객체**
- 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 됨 --------- private 로 만들거나, 상황에 따라 선택 ( 해본 거네 ) 
- 참고 : Integer, String 은 자바가 제공하는 대표적인 불변 객체**

> Address 객체 값을 수정해야 할때는 ? Address 객체를 새로 만들어서(or copy) 통으로 바꿔버리는게 맞음 

> '불변이라는 작은 제약으로 부작용(side effect)이라는 큰 재앙을 막을 수 있다.'

### 4. 값 타입의 비교 
- 값 타입 : 인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야함 
- **동일성(identity)** 비교 : 인스턴스의 참조 값을 비교, == 사용 
- **동등성(equivalence)** 비교 : 인스턴스의 값을 비교, equals() 사용
- 값 타입은 a.equals(b)를 사용해서 동등성 비교를 해야 함 
- 값 타입의 equals() 메소드를 적절하게 재정의(주로 모든 필드 사용)
> equals(), hashCode() @Override template 자동 생성 기능 지원하니 활용하면 좋음 

#### 값 타입 컬렉션 
- 값 타입을 하나 이상 저장할 때 사용 
- @ElementCollection, @CollectionTable 사용 
- DB는 컬렉션을 같은 테이블에 저장할 수 없다
  - MEMBER 와 FAVORITE_FOOD/ADDRESS 테이블은 각각 1대 다의 관계로 표현되기 때문에
- 컬렉션을 저장하기 위한 별도의 테이블이 필요함 

```java  
@Entity
public class Member {
 
    ...
 
    @ElementCollection // default LAZY
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
        @JoinColumn(name="MEMBER_ID")
    ) // FAVORITE_FOOD 테이블에 MEMBER_ID FK 생성 (join 용)
    @Column(name = "FOOD_NAME") // 예외적으로 컬럼명 지정해줌
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns =
        @JoinColumn(name = "MEMBER_ID")
    ) // ADDRESS 테이블에 필드명 동일, MEMBER_ID FK 추가된 형태로 테이블 생성
    private List<Address> addressHistory = new ArrayList<>();

}
```

- MEMBER 만 저장하는데 값타입 컬렉션도 같이 들어감 -> MEMBER 생명주기에 소속됨 (ex. username 도 값 타입)
  - 값 타입 컬렉션도 지연 로딩 전략 사용 
- 참고 : 값 타입 컬렉션은 영속성 전에(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.
```java 
//JpaMain 에서 insert 예시 
  try{
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("city1", "street", "zipcode"));
            
            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족팔");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new Address("old1","street","zipcode"));
            member.getAddressHistory().add(new Address("old2","street","zipcode"));
    
            em.persist(member); //MEMBER 테이블 1회 , FAVORITE_FOOD 테이블 3회, ADDRESS 테이블 2회 insert 발생함  
           
            em.flush();
            em.clear();
            
            // DB에 저장되어 있고, 영속성 컨텍스트는 비어있는 상태
            Member findMember = em.find(Member.class, member.getId()); // select MEMBER 만 나감( 값 타입 컬렉션은 지연로딩)

            List<Address> addressHistory = findMember.getAddressHistory();
            for (Address address : addressHistory) {
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
            findMember.getFavoriteFoods().add("한식"); // insert 문 나감, 값 타입의 특징 뭐라뭐라.., 요거는 쿼리 insert 한번만 나감 

            // equals() 와 hashCode()구현 꼭 해주기 ! -> 컬렉션에서 해당 메소드로 객체찾음
            findMember.getAddressHistory().remove(new Address("old1","street","zipcode")); // delete 문 실행, 다 지움(특)
            findMember.getAddressHistory().add(new Address("newCity","street","zipcode")); // insert 문 실행, 근데 old2가 살아있어서 old2, newCity insert 문 총 2개 실행됨!!(특이)
            
            tx.commit();
  } catch (Exception ex) {
    ...
  } 
```
> Set , List 컬렉션에서 데이터 삭제 후 저장 했을 때 실행 query 가 다르게 출력되는 이유? (set은 한번씩 실행되는데, List 는 다른 객체 살아있으면 그 객체 수만큼 추가로 insert 또 실행)

#### 값 타입 컬렉션의 제약사항 -- 이것도 위험하다함💣.. 복잡하면 다르게 풀어야 한다함 
- 값 타입은 엔티티와 다르게 식별자 개념이 없다. 
- 그래서 값을 변경하면 추적이 어렵다.
- (중요)값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장함
- 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야 함 **(null 입력 X, 중복 저장 X)**

> @OrderColumn(name = "address_history_order") 추가하면 순서컬럼/값이 들어가면서 update 문만 실행됨(리스크 우회방법)
> 근데 이 방식도 위험하다함💣 

#### 값 타입 컬렉션 대안 -- 실무에서 이렇게 많이 쓴다함
- 실무에서는 상황에 따라 **값 타입 컬렉션 대신에 일대다 관계를 고려**
- 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용 
- 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용 
  - ex) AddressEntity -- **실제 운영에서 많이 쓴다함😎**
```java
// 값 타입을 엔티티로 승급시켜서 사용하는 예제 , 테이블에 id가 추가되면 더이상 값 타입이 아니라 엔티티라고 이해하는 듯 함
    @Entity
    public class Member {
    
      @Id
      @GeneratedValue
      @Column(name = "MEMBER_ID")
      private Long id;
    
      @Column(name = "USERNAME")
      private String username;
    
      @Embedded
      private Period workPeriod;
    
      @Embedded
      @AttributeOverrides({
              @AttributeOverride(name = "city",
                      column = @Column(name = "WORK_CITY")),
              @AttributeOverride(name = "street",
                      column = @Column(name = "WORK_STREET")),
              @AttributeOverride(name = "zipcode",
                      column = @Column(name = "WORK_ZIPCODE"))
      })
      private Address workAddress;
    
      @Embedded
      private Address homeAddress;
    
    
      @ElementCollection
      @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
      @JoinColumn(name = "MEMBER_ID")
      ) // FAVORITE_FOOD 테이블에 MEMBER_ID FK 생성 (join 용)
      @Column(name = "FOOD_NAME") // 예외적으로 컬럼명 지정해줌
      private Set<String> favoriteFoods = new HashSet<>();
      
      /*    @OrderColumn(name = "address_history_order")
          @ElementCollection
          @CollectionTable(name = "ADDRESS", joinColumns =
              @JoinColumn(name = "MEMBER_ID")
          ) // ADDRESS 테이블에 필드명 동일, MEMBER_ID FK 추가된 형태로 테이블 생성
          private List<Address> addressHistory = new ArrayList<>();*/
    
      @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
      @JoinColumn(name = "MEMBER_ID")
      private List<AddressEntity> addressHistory = new ArrayList<>(); 
    
      //... 생략
    }
    
    // AddressEntity 생성 
    @Entity
    @Table(name = "ADDRESS")
    public class AddressEntity {

      @Id @GeneratedValue
      private Long id;

      private Address address;

      public AddressEntity(){}

      public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);   // 요런식으로 우회가능
      }

      public Long getId() {
        return id;
      }

      public void setId(Long id) {
        this.id = id;
      }
    }
    
    
    // JpaMain 수정 

    Member member = new Member();
    member.setUsername("member1");
    member.setHomeAddress(new Address("city1", "street", "zipcode"));

    member.getFavoriteFoods().add("치킨");
    member.getFavoriteFoods().add("족팔");
    member.getFavoriteFoods().add("피자");

    member.getAddressHistory().add(new AddressEntity("old1","street","zipcode")); // 값타입이 엔티티로 승급한다고 표현함
    member.getAddressHistory().add(new AddressEntity("old2","street","zipcode")); // insert, update문 실행됨(일대다 단방향 매핑이기 때문..다시 공부) 

    em.persist(member);

    em.flush();
    em.clear();
    //DB에 저장되어 있고, 영속성 컨텍스트는 비어있는 상태
    Member findMember = em.find(Member.class, member.getId()); // select MEMBER 만 나감( 값 타입 컬렉션은 지연로딩)

```
> 값 타입 컬렉션은 진짜 간단할때(ex. select box) 사용, 따로 조회하거나 변경해야 하는 경우 Entity로 만들어서 사용해야 함 

#### 정리 
- **엔티티 타입 특징**
  - 식별자 O 
  - 생명 주기 관리 
  - 공유 
- **값 타입의 특징**
  - 식별자 X 
  - 생명 주기를 엔티티에 의존 
  - 공유하지 않는 것이 안전(복사해서 사용)
  - 불변 객체로 만드는 것이 안전 
- 값 타입은 정말 값 타입이라 판단될 때만 사용 
- 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안됨 ! 
- 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티

#### 실전 예제 
- MEMBER Entity 에 address 적용

### 5. 값 타입 컬렉션 

### 6. 실전 예제 - 값 타입 매핑 