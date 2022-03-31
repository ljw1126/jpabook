## 프록시와 연관관계 정리 

### 프록시 
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