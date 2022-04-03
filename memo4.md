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

### 5. 값 타입 컬렉션 

### 6. 실전 예제 - 값 타입 매핑 