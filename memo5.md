## 객체 지향 쿼리 언어1 - 기본 문법

### 1. 소개

#### JPA 는 다양한 쿼리 방법을 지원 
- **JPQL**  
- JPA criteria 
- **QueryDSL** 
- native SQL 
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함계 사용 

#### JPQL 소개 
- 가장 단순한 조회 방법 
  - EntityManager.find()
  - 객체 그래프 탐색(a.getB().getC())
  - 만약 나이가 18살 이상인 회원을 모두 검색하고 싶다면 ? 
- JPA를 사용하면 엔티티 객체를 중심으로 개발 
- 문제는 검색쿼리 
- 검색을 할 때도 **테이블이 아닌 엔티티 객체를 대상으로 검색** 
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능 
- application이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요 
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공 
- SQL 과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원 
- **JPQL은 엔티티 객체를 대상으로 쿼리** // 엔티티 대상으로 하니 표현 방식이 다름 -> 실행시 SQL 문법으로 매핑됨
- SQL은 DB 테이블을 대상으로 쿼리 
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존 x  // 추상화 했으니 여러 DBMS 바꿔서 사용가능, DBMS 문법 매핑 지원함

> JPQL을 한마디로 정의하면 객체 지향 SQL 

//검색 JPQL 예시 생략(7분, ppt 참고)

- 단점. JPQL 은 동적 쿼리 만들기 굉장히 어려움 // 조건문에 짜르기 더하기로 string query 생성 ㅋㅋ  

#### Criteria 소개 
//예시 (12분, ppt 참고)
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- 표준 문법 스펙 지원함(자바 1.6) 
- JPQL 빌더 역할 
- JPA 공식 기능 
- 장점 
  - 오타 날 경우 컴파일 시점에 에러 확인가능 
  - 동적 쿼리 짜기 쉬움 
- 단점 
  - SQL 스럽지 않음  // 실무에서 안씀. 운영/유지보수가 안되서.. 망한 스펙 같다고 함.
  - 디버깅 어려움 // 결국 너무 복잡하고 실용성 없음..
- Criteria 대신 **QueryDSL(오픈소스 라이브러리) 사용 권장**

#### QueryDSL 소개  -- 실무 사용 권장👍
//예시 (19분)
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- JPQL 빌더 역할 
- 컴파일 시점에 문법 오류를 찾을 수 있음 
- 동적 쿼리 작성 편리함
- **단순하고 쉬움, 직관적**   // Criteria 보다 좀 더 보기 쉽네 . jpql 알면 querydsl도 습득가능하다 함

> www.querydsl.com  > Reference doc 가면 setting 이랑 문법 어떻게 사용하는지 잘 나와 있음 !!

#### native SQL 소개 
//예시 (25분)
- JPA가 제공하는 **SQL을 직접 사용하는 기능**   // = 진짜 SQL문 사용가능 
- JPQL로 해결할 수 없는 특정 DB에 의존적인 기능 
- 예) 오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트(<-> hibernate에서 셋팅해서 기능 제공한다함)

#### JDBC 직접 사용, SpringJdbcTemplate 등 
- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, Mybatis 등을 함께 사용 가능 
- 단, 영속성 컨텍스트를 적절한 시점에 강제로 flush 필요 
  - flush 되야(persist -> commit 할 때) DB에 데이터가 들어가는데..
  - em.createNativeQuery() 실행시 바로 flush 되고 commit(이 시점에도 flush) 됨 // 전략 
  - 문제가 되는 예시에 대해 설명함 (30분)
- 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 flush

### 2. 기본 문법과 쿼리 API 

### 3. 프로젝션(SELECT)

### 4. 페이징 

### 5. 조인 

### 6. 서브쿼리 

### 7. JPQL 타입 표현과 기타식 

### 8. 조건식(CASE 등등)

### 9. JPQL 함수 