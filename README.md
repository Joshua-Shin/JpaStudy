## SpringBoot와 JPA를 활용한 웹 애플리케이션 개발 프로젝트
- 본 프로젝트는 <자바 ORM 표준 JPA 프로그래밍 : 기본편> 강의를 수강하며 진행한 실습 프로젝트 입니다.
- 사용 도구 : Java 11, JPA, IntelliJ, H2 database(1.4.200)
-------
### 메모
- 개요
  - Persistence가 설정 조회해서 H2 데이터베이스와 연동된 EntityManagerFactory를 생성
  - EntityManagerFactory가 EntityManager를 생성
  - 마치 자바 컬렉션 프레임 워크 다루듯이 데이터를 저장, 조회, 갱신, 삭제 할 수 있음
  - .persist(), .remove(), .find()
  - find 해서 받은 인스턴스.setName();
  - 전체 검색 하고 그럴때는 JPQL 사용.
  - 일종의 객제 지향 SQL 임.


- 영속성 컨텍스트
  - 마치 스프링쪽에서 스프링컨테이너랑 비슷한 개념인듯.
  - AnnnotationConfig..어쩌구 해서 스프링 컨테이너 가져온뒤 그것을 가지고 컨테이너에 있는 bean들을 사용했던것처럼
  - JPA에서도 자바와 DB를 연동해주는 그 사이에서 작동을 하는 애가 있네.
  - 그리고 그러한 영속성 컨텍스트는 EntityManager가 관리를 하고, 트랜젝션이 commit할때까지 존재함
  - DB에서 조회를 하든, 자바쪽에서 데이터를 저장, 수정, 삭제를 하든 일단은 영속성 컨텍스트에서 1차적으로 붙잡고 있음
  - 1차 캐시 역할을 하기도 하는데, 클라이언트쪽에서 호출을 할때마다 새로운 영속성컨텍스트가 만들어지기 때문에, 캐시에서 얻는 성능상의 이점은 크게는 없음
  - 버퍼의 역할을 해주기에 sql를 모아서 날려줌.
  - 때문에 영속성컨테이너가 commit 하지 않은 상태에서 같은 데이터를 find 한다거나 하면, DB에 쿼리를 여러번 날리는게 아니라, 한번 날린 후에는 1차 캐시에서 가져옴
  - find 이외의 명령들에도 마찬가지고.
  - flush는 commit 하거나, 쿼리 날릴때 진행됨.
  - detach(entity), em.clear(), em.close()를 통해 준영속 상태로 만들 수도 있음.
  - 비영속은 아직 영속성컨테이너에 들어간적이 없는거고, 준영속은 한번 들어갔다가 분리된거야.


- 요구사항 분석과 기본 맵핑
  - 일단 언어 혼용 방지하기 위해. JPA 및 자바쪽에서는 엔티티, 필드를 DB 에서는 테이블, 칼럼이라 하는듯
  - persistence.xml 보면 ddl 스키마 자동생성 옵션을 바꿀 수 있어.
  - create, create-drop, update, validate, none
  - create는 테이블 다 드랍 후 다시 insert,
  - create-drop 은 drop, insert, 끝날때 drop
  - update는 변경 사항만 올려주고
  - validate는 매칭이 맞나 확인만 해주고
  - none은 그냥 관례상 키워드를 none이라 쓰는데, 아예 해당 옵션 줄을 삭제한거랑 같음.
  - **운영 서버에서는 당연히!!! create, create-drop, update 하면 안돼.**
  - update도 변경 안되는 애들도 락이 걸려서 서버가 멈추고 그럴 수 있어.
  - @Entity, @Id, @GeneratedValue, 
  - @Column(name = "member_id"), @Column(nullable = false, length = 10)
  - @Enumerated(EnumType.STRING)
  - 이 정도는 반드시 기억. @GeneratedValue에서 전략이 여러가지가 있는데, 주로 그냥 디폴트로 두는듯
