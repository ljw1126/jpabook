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
    public static void main(String[] args) {
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

### 4. 값 타입의 비교 

### 5. 값 타입 컬렉션 

### 6. 실전 예제 - 값 타입 매핑 