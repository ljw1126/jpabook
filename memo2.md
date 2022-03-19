## 고급매핑 
### 01. 상속관계 매핑 
```
- 관계형 db는 상속 관계 x 
  - 슈퍼타입 서브 타입 관계라는 모델링 기법이 객체 상송과 유사 
- 상속 관계 매핑 : 객체의 상속과 구조와 DB의 슈퍼타입 서브타입 관계를 매핑 
  (이미지 참고)

- 슈퍼 타입 서브 타입 논리 모델을 실제 물리 모델로 구현하는 방법 
  - 조인 전략 : pk 동일하게 두고 구분 필드 사용
  - 단일 테이블 전략 : 한 테이블에 넣고 구분 필드 사용(단순) 
  - 구현 클래스 마다 테이블 전략 : 각각 테이블 다 만듦(중복이긴해도) 

- 주요 어노테이션 
  - @Inheritance(strategy=Inheritancetype.XXX)
    - JOINED : 조인 전략 
    - SINGLE_TABLE : 단일 테이블 전략 
    - TABLE_PER_CLASS : 구현 클래스마다 테이블 전략 
  - @DiscriminationColumn(name="DTYPE")
  - @DiscriminatorValue("XXX")
  
```

#### 단일 테이블 전략(default)
```java 
@Entity
public class Item {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
}

@Entity
public class Album extends Item{
    private String artist;
}

@Entity
public class Book extends Item{
    private String author;
    private String isbn;
}

@Entity
public class Movie extends Item{
    private String director;
    private String actor;
}
```

##### 실행시 create query 
```
create table Item (
       DTYPE varchar(31) not null,
        id bigint not null,
        name varchar(255),
        price integer not null,
        author varchar(255),
        isbn varchar(255),
        actor varchar(255),
        director varchar(255),
        artist varchar(255),
        primary key (id)
)
```

#### [1. JOINED 전략]
```
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
}
```
##### 실행시 query 
```
 create table Album (
       artist varchar(255),
        id bigint not null,
        primary key (id)
    )
Hibernate: 
    
    create table Book (
       author varchar(255),
        isbn varchar(255),
        id bigint not null,
        primary key (id)
    )

    create table Item (
       id bigint not null,
        name varchar(255),
        price integer not null,
        primary key (id)
    )
Hibernate: 
    
    create table Movie (
       actor varchar(255),
        director varchar(255),
        id bigint not null,
        primary key (id)
    )
Hibernate: 
    
    alter table Album 
       add constraint FKcve1ph6vw9ihye8rbk26h5jm9 
       foreign key (id) 
       references Item
Hibernate: 
    
    alter table Book 
       add constraint FKbwwc3a7ch631uyv1b5o9tvysi 
       foreign key (id) 
       references Item
Hibernate: 
    
    alter table Movie 
       add constraint FK5sq6d5agrc34ithpdfs0umo9g 
       foreign key (id) 
       references Item

```
##### movie insert시 
```
SELECT * FROM ITEM ;
ID  	NAME  	PRICE  
1(pk)  위대한 쇼	10000
(1 row, 1 ms)

SELECT * FROM MOVIE ; // ITEM ID와 동일
ACTOR  	DIRECTOR  	ID  
john	michel bay	1 (pk,fk)

```

##### movie select시 
``` 
select
        movie0_.id as id1_2_0_,
        movie0_1_.name as name2_2_0_,
        movie0_1_.price as price3_2_0_,
        movie0_.actor as actor1_3_0_,
        movie0_.director as director2_3_0_ 
    from
        Movie movie0_ 
    inner join
        Item movie0_1_ 
            on movie0_.id=movie0_1_.id 
    where
        movie0_.id=?
```

##### dtype (default) 추가 및 값 설정 
```
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn      //Dtype 필드 생성됨 , 있는게 좋음. Entity명 들어감 , name 속성으로 필드명 변ㄴ경가능
public class Item {
    ...
}

@Entity
@DiscriminatorValue("A")    // Dtype 필드에 들어갈 값을 A로 지정(default는 Entity명)
public class Album extends Item{
    ...
}

@Entity
@DiscriminatorValue("B") // Book insert시 Item의 dtype에 "B"값이 저장됨
public class Book extends Item{
    ...
}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item{
    ...
}

```

#### [2. 단일 테이블 전략]
```
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 요거만 변경하면 됨 , DTYPE default로 생성됨   
public class Item {
    ...
}

@Entity
@DiscriminatorValue("A")    
public class Album extends Item{
    ...
}

@Entity
@DiscriminatorValue("B") 
public class Book extends Item{
    ...
}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item{
    ...
}

```

##### 단일 테이블 전략 create query 
```
create table Item (
       DTYPE varchar(31) not null, // DTYPE으로 구분
        id bigint not null,
        name varchar(255),
        price integer not null,
        author varchar(255),
        isbn varchar(255),
        actor varchar(255),
        director varchar(255),
        artist varchar(255),
        primary key (id)
    )
```

※ 운영상 DType 생성하는게 좋다! 
> 단일테이블이든, JOIN 전략이든 변경시 좀 더 수워함 -> 쿼리를 고칠 필요없으니

#### [3. 구현 클래스 마다 테이블 전략] 
```
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 변경
public abstract class Item { // 추상클래스로 변경
    ...
}

```

##### create query 
``` 
// item 테이블이 없음 !! 
Hibernate: 
    
    create table Album (
       id bigint not null,
        name varchar(255),
        price integer not null,
        artist varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table Book (
       id bigint not null,
        name varchar(255),
        price integer not null,
        author varchar(255),
        isbn varchar(255),
        primary key (id)
    )
Hibernate: 
    
    create table Movie (
       id bigint not null,
        name varchar(255),
        price integer not null,
        actor varchar(255),
        director varchar(255),
        primary key (id)
    )
```
- (장점)단순하게 값을 넣고 뺄때는 좋음 
- (단점) 조회시 union all 로 조회함 
```
 Item findItem = em.find(Item.class, movie.getId()); // 부모 추상 클래스로 조회 가능 
 System.out.println(findItem); // 아래 union all 쿼리 실행됨
 
 Hibernate: 
    select
        item0_.id as id1_2_0_,
        item0_.name as name2_2_0_,
        item0_.price as price3_2_0_,
        item0_.author as author1_1_0_,
        item0_.isbn as isbn2_1_0_,
        item0_.actor as actor1_3_0_,
        item0_.director as director2_3_0_,
        item0_.artist as artist1_0_0_,
        item0_.clazz_ as clazz_0_ 
    from
        ( select
            id,
            name,
            price,
            author,
            isbn,
            null as actor,
            null as director,
            null as artist,
            1 as clazz_ 
        from
            Book 
        union
        all select
            id,
            name,
            price,
            null as author,
            null as isbn,
            actor,
            director,
            null as artist,
            2 as clazz_ 
        from
            Movie 
        union
        all select
            id,
            name,
            price,
            null as author,
            null as isbn,
            null as actor,
            null as director,
            artist,
            3 as clazz_ 
        from
            Album 
    ) item0_ 
where
    item0_.id=?

```

#### 장단점
[조인전략]  // default 정석, 객체하고 비슷하니, 비지니스적으로 선호
- 장점 
  - 테이블 정규화
  - 외래 키 참조 무결성 제약조건 활용가능 
  - 저장공간 효율화 
- 단점 
  - 조회시 조인을 많이 사용, 성능 저하 // 그렇게 저하가 되지 않음 
  - 조회 쿼리가 복잡함 
  - 데이터 저장시 INSERT SQL 2번 호출 // 그렇게 큰 단점은 아님
  - 단일 테이블보다 테이블 갯수가 많아 복잡, 성능 덜 나올 수 있음 

[단일 테이블 전략] // 확장 가능성 없으면 괜찮음
- 장점 
  - 조인이 필요 없으므로 일반적으로 조회 성능이 빠름 
  - 조회 쿼리가 단순함 
- 단점 
  - 자식 엔티티가 매핑한 컬럼은 모두 null 허용 (무결성 애매)
  - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. // 보통 임계점 넘을 일은 없다함 
  - 상황에 다라서 조회 성능이 오히려 느려질 수 있다.

[구현 클래스] //💩 이거는 쓰면 안된다함
- **이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천 x**  
- 장점 
  - 서브 타입을 명확하게 구분해서 처리할 때 효과적 (insert, select 효과적) 
  - not null 제약조건 사용 가능 
- 단점 
  - **여러 자식 테이블을 함께 조회할 때 성능이 느림** (UNION SQL 사용)
  - 자식 테이블을 통합해서 쿼리하기 어려움 
  - 시스템에 새로운 타입 추가될때 많이 고쳐야 함 .. (Entity 하나 추가할때마다 union 늘어지겠네)

> 논리모델을 객체 물리모델로 구현하는 방법 3가지에 대해 설명함(default join table로 하고 단일 테이블이랑 장단점 기억해뒀다가 상황봐서 선택하기)

- 