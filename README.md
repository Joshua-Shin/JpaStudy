## <자바 ORM 표준 JPA 프로그래밍 : 기본편> - 김영한
- 본 저장소는 <자바 ORM 표준 JPA 프로그래밍 : 기본편> 강의를 수강하며 진행한 실습 예제와 강의 내용을 기록한 곳입니다.
- 환경 설정 : Java 11, JPA, IntelliJ, H2 database(1.4.200)
-------
### 단원별 요점 정리

#### 수업 전체 목표
- 객체와 테이블을 제대로 설계하고 매핑하기
- JPA의 내부 동작 방식 이해하기

#### JPA 소개
- JPA는 ORM(Object-relational mapping(객체 관계 매핑)) 프레임워크 중 하나. <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 02 19" src="https://user-images.githubusercontent.com/93418349/234257774-d0a918e0-414a-4d6c-be24-70cddbacea6c.png"> <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 02 25" src="https://user-images.githubusercontent.com/93418349/234257796-4cbcce24-6858-404a-b5c0-86270498576e.png"> <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 02 29" src="https://user-images.githubusercontent.com/93418349/234257806-12dfedac-5766-4050-9182-e9800f7eeeb8.png">

#### JPA 시작하기
- JPA 구동방식 <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 08 16" src="https://user-images.githubusercontent.com/93418349/234259228-5071f39b-dada-47b3-bd54-147cead5dda6.png">
- 엔티티매니저팩토리는 웹서버당 하나만 생성되고, 엔티티 매너지는 매 요청당 새로 생성됨. 마치 스프링에서 싱글톤객체와 웹스코프의 request 객체같네.
- JPA의 모든 데이터 변경은 트랜잭션 안에서 진행.
- 마치 자바 컬렉션 프레임 워크 다루듯이 데이터를 저장, 조회, 갱신, 삭제 할 수 있음
- .persist(), .remove(), .find(), .setName()
- 전체 검색 하고 그럴때는 JPQL 사용.
  - 일종의 객제 지향 SQL 임.
  
#### 영속성 관리 - 내부 동작 방식
- 영속성 컨텍스트 : 엔티티를 영구 저장하는 환경
- 스프링이 들어오면 조금 달라지긴 하는데 일단은 entityManager 가 영속성 컨텍스트라 생각하고 봐.
- DB에서 조회를 하든, 자바쪽에서 데이터를 저장, 수정, 삭제를 하든 일단은 영속성 컨텍스트에서 1차적으로 붙잡고 있음
- 데이터베이스에서 조회 <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 20 14" src="https://user-images.githubusercontent.com/93418349/234261867-5a308daa-a5bb-41a5-916b-7aaa25ec23a3.png">
- 때문에 조회시 처음부터 db를 뒤지지 않고 1차 캐시에서 찾아오고, 없으면 db까지 sql 날려서 조회함.
- 1차 캐시 역할을 하긴 하지만, 클라이언트쪽에서 호출을 할때마다 새로운 영속성컨텍스트가 만들어지기 때문에, 캐시에서 얻는 성능상의 이점은 크게는 없음.
- 쓰기 관련 sql도 모아서 날려줌.
- 변경 감지 
- 변경된 사항을 em.update 뭐 이런거 안해줘도 플러쉬될때 알아서 변경된 사항을 감지해서 반영해줌. <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 20 39" src="https://user-images.githubusercontent.com/93418349/234262342-d2e0de77-e20f-41a2-a624-0bb70c4018c6.png">
- flush
  - 영속성 컨텍스트의 변경 내용을 db에 반영
  - **flush 되는 시점**
    - tx.commit(), JPQL 쿼리 날릴때, em.flush().
  - 영속성 컨텍스트를 비운다거나 1차 캐시를 지운다거나 하는게 아님을 주의.
- 엔티티의 생명주기 <br> <img width="600" alt="스크린샷 2023-04-25 오후 8 19 54" src="https://user-images.githubusercontent.com/93418349/234261735-aff5c22f-a32e-4329-9ae6-e85fe3d0dd2a.png">
- detach(entity), em.clear(), em.close()를 통해 준영속 상태로 만들 수도 있음.
- 비영속은 아직 영속성컨테이너에 들어간적이 없는거고, 준영속은 한번 들어갔다가 분리된거야.

#### 엔티티 매핑
- 일단 언어 혼용 방지하기 위해. JPA 및 자바쪽에서는 엔티티와 필드를, DB 에서는 테이블과 칼럼이라 하는듯
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
- @GeneratedValue에서의 전략. Identify 전략, sequence 전략
  - identify 전략은 각 객체마다 키를 다르게 생성해서 영속성컨텍스트에 올라갈때 넣어주는거고
  - sequence 전략은 db에서 키를 만들어서 키를 db한테 얻은 후 영속성 컨텍스트에 올리는거야
  - 키가 없으면 영속성 컨텍스트에 올릴 수 없어.
  - 때문에, sequence 전략은 commit 하기 전에, persist하면 hibernate_sequence한테 키 달라고 요청해 키 얻은 후
  - commit 하는 순간 그때 insert 하게 됨. persist 할때 insert 하는건 아니야.
  - 단점은 db 전체를 통합해서 키를 던져주기때문에, 객체별로 다르게 1부터 넘버링 하지 않는다는점.


#### 연관관계 매핑 기초
- 연관관계가 객체에서는 참조, 테이블에서는 FK를 통해 이루어짐
- 지금처럼 각 엔티티에다가 개별 ID를 뽑아 멤버변수로 넣는것은 객체 지향 스타일은 아니고 철저히 RDB 느낌
- 따라서 참조 형태로 매핑을 할거야.
- 일대다에서 FK를 가지는쪽은 항상 다에 있고, 해당 필드를 참조형태로 바꿀꺼고. 이 객체를 연관관계의 주인이라 함
- 주인이라고 그래서 뭐 비즈니스적으로 중요한 객체인것 같지만, 사실은 반대편의 객체가 더 중요함.
- 바퀴랑 자동차를 생각해봐. 바퀴랑 자동차는 다대일 인데, 바퀴가 연관관계 주인이 되겠지. 그러나 중요한건 자동차.
- 실제 DB에서도 FK를 갖는 쪽을 자식 엔티티, 반대편을 부모엔티티라 하잖아.
- 다만, 테이블의 경우 FK 가지고 두 테이블이 join을 하게 되면 서로가 서로에게 조회할 수 있는 양방향성인데,
- 객체의 경우 참조 변수를 가지고 있는 객체쪽에서만 참조 인스턴스를 조회할 수 있는, 단방향성이야.
- 때문에, 연관관계의 주인의 반대편 쪽에서도 연관관계 주인 쪽으로 조회할 수 있는 즉, 가짜 매핑 필드를 넣어주어서 양방향으로 접근 가능하게 만들기도 해.
- 다만, 양방향이 되면 객체 입장에서는 여러가지 번거로운게 많아짐
- 가짜 매핑 필드는 DB에 직접 쓰기는 안되고 읽기만 돼.
- commit 하기전에 변경된 사항을 가짜 매핑필드가 읽기를 하게 되면, 당연히 반영이 안되겠지. 때문에 중간에 em.flush(), em.clear()를 해주고,
- 다시 DB에서 데이터를 가져오기 떄문에 그때는 반영된걸 가져옴
- 다만, JPA, DB 다 떼고 순수 자바 코드 영역에서 테스트코드를 작성한다거나 할때 이 부분이 문제기 때문에, 애초에 값을 넣어줄때 양쪽에 넣어준다.
- 각자의 set 메소드를 두개 호출하지 말고 두개의 set 메소드를 하나의 메소드로 묶어서 그 메소드를 호출하는 형태로 만들어서 사용하는 편이 좋아.
- 다만 set이라는 키워드는 관례상 의미가 고정적이기에 changeOrder(), addOrder() 뭐 이런식으로 다른 이름을 붙이는게 좋아.


#### 다양한 연관관계 매핑
- 다대일 : 가장 추천, FK가 있는 테이블의 엔티티에 연관관계의 주인을 두는거야.
- 일대다 : (사실 다대일과 같은 의미이지만 여기에서는 연관관계의 주인이 일 쪽에 있는 상황으로 해석)는 연관관계의 주인이 반대쪽 테이블에 매핑이 되는건데,
  가능은 해. 단방향 뿐만 아니라 양방향 역시 가능은 해. 
  - 반대편 엔티티에서 (@ManyToOne에는 mappedBy 옵션이 없기 때문에) JoinColumn의 옵션에 insertable=false, updatalbe=false 줘서 읽기전용으로 억지로 만들면.
  - 그러나 비추. 그냥 다대일 양방향 매핑 써라.
- 일대일 : 어느쪽이든 연관관계의 주인을 가져가는게 가능하겠지. 보통 JPA 입장에서는 주 엔티티가 가져가는게 좋긴 좋아. 
  - 다만 DB 입장에서는 후에 일대일 관계가 일대다 관계로 바뀌게 될 경우, 그 다 쪽이 주 엔티티가 아닐 가능성이 크고 그러면 FK를 가져간 주 엔티티를 다 뜯어 고쳐야 하니
    주 엔티티의 반대쪽이 FK를 가져가게 하는게 좋긴함.
- 다대다 : JPA에서 ManyToMany로 문법상 지원을 하고, 자동으로 테이블을 형성하긴하는데, 하지말고, 새로운 테이블에 맞춰 새로운 엔티티 만들어서 해.
- @JoinColumn 에서의 name 옵션은 단순히 테이블의 칼럼을 지정하는 역할 뿐이다.
- ```
  public class Order {
    // (... 생략 ...) 
    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
  }

  -----------------------------

  public class Delivery {
    @Id @GeneratedValue
    private Long id;
  }
  ```
- 분명 Delivery 엔티티는 PK 필드인 id 에다가 따로 @Column(name = "delivery_id")을 붙이지 않았음.
- 그럼에도 Order 엔티티에서 Delivery를 FK키로 갖고 참조 할때, 조인 칼럼을 "delivery_id" 이라 해도 정상적으로 기능한게 좀 의아했는데
- 찾아보니 @JoinColumn(name = "delivery_id") 이건 Order 엔티티에서 해당 참조 값을 테이블에서 어떤 칼럼명으로 담을꺼냐 하는거고
  그것과 무관하게 JPA에서 알아서 Delivery 엔티티의 PK값을 찾아 매핑을 해줌.
- [질답 링크](https://www.inflearn.com/questions/399160/joincolumn-%EA%B4%80%EB%A0%A8%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4)


#### 고급 매핑
- DB에서 논리적 모델링이 슈퍼, 서브 테이블 구조일때 객체에서 어떻게 할것인가.
- 물리적 모델링의 3가지 모델에 따라 JPA 쪽에서도 3가지 전략이 있어
- 조인 전략, 통합 테이블 전략, 클래스 별 테이블 전략
- 조인 전략: 가장 객체 스럽고 정규화된 DB에 알맞고 확장성도 좋으니 일반적으로 좋아.
- 통합 테이블 전략 : 조인 전략은 조회할때 join을 여러번 해야할 수 있어서 성능 이슈 생길 수도 있으니, 그냥 한 테이블에 다 때려넣어서 조회할 때 유리,
- 조인 전략이랑 통합 테이블 전략의 장/단은 정규화 반정규화의 장/단점이랑 비슷한 느낌. 상황에 따라 유불리가 달라.
- 클래스 별 테이블 전략 : 상위 하위 클래스를 다 테이블로 만드는건데 이 전략은 쓰지마. JPA쪽도 DB 쪽도 다 싫어함.
- 세팅
  - 상위, 하위 클래스 모두에다 @Entity 붙이고
  - 상위 클래스는 따로 객체 만들일이 없다면 안전하게 abstract 붙여서 추상클래스로 만들고
  - 상위 클래스에다가 각 전략에 맞게 @Inheritance(strategy = InheritanceType.XXX) 붙이고,
  - xxx에 들어갈 키워드 : JOINED, SINGLE_TABLE, TABLE_PER_CLASS
  - 상위클래스에다가 @DiscriminatorColumn 붙이면 끝.
- DB 구조가 달라도 코드를 바꿀 필요 없이 어노테이션의 전략 키워드만 수정하면 됨.  
- @MappedSuperClass
  - DB와의 상관관계 매핑과 관계 없이, 그냥 모든 엔티티에 들어갈만한 속성을 매 클래스에 필드 복붙하기 귀찮으니까
  - BaseEntity 같은 추상클래스 만들어서 **공통 속성**을 필드로 넣고 해당 클래스를 다른 엔티티들이 상속하게 만듬
  - 공통 속성의 예로는 생성일, 업데이트일, 수정한 사람, 뭐 이런것들.
  - 엔티티가 아니니 영속성에 올라가지도 않고 테이블도 아니야.
  - 모든 객체에다가 extends 해버리면 되는데, 다대다 매핑 할때 생성되는 중간 테이블은 BaseEntity를 상속할 수 없으니 필드들이 당연히 안들어감. 이러니 다대다가 안좋아.


#### 프록시와 연관관계 관리
- 지연 로딩 전략
  - em.find(entity) 할 경우 해당 entity와 연관관계를 갖고 있는 애들을 쿼리 이것저것 날려서 다 가지고 와버림.
  - 실제 엔티티가 아니라 프록시(가짜)를 가져온뒤 나중에 실제 엔티티가 필요한 상황이 올때 프록시가 걔를 가져오게 하는 전략.
  - 을 사용하면 연관관계에 있는 애들을 다~ 가져오는 상황을 막을 수 있어.
  - 특히 이게 JPQL 등으로 쿼리를 날릴때는 더 sql 낭비가 심해지니 실무에서는 꼭 이 같은 **지연 로딩 전략**을 사용해야돼.
  - @XXXToOne 붙은 애들의 디폴트 fetch는 즉시 로딩이야. 따라서 얘네들은 꼭 지연 로딩으로 명시적으로 바꿔줘야해
  - @ManyToOne(fetch = LAZY)
- 영속성 전이
  - 자식 엔티티가 부모엔티티를 **하나만 가지고 있을 경우**, 영속성을 전이 시키는편이 관리하기 좋아
  - 영속성을 전이 시키지 않는다면, em.persist(parent); em.persist(child1); em.persist(chil2); 줄줄줄...써야돼
  - 영속성 전이 시키면, em.persist(parent) 하나로 다 알아서 끝.
  - 방법은 부모 엔티티쪽에 @OneToOne(cascade = ALL)


#### 값 타입
- 속성이 비슷한 필드들을 새로운 클래스로 묶는거야
- 클래스 내에서 새로운 비즈니스 로직과 관련된 메소드를 만들 수도 있고, 여러 이점들이 있어
- 다만 자바에서는 기본 타입은 '=' 연산을 할 경우 값이 복사 되지만, 객체 타입은 '=' 연산을 할 경우 참조 값이 전달되어버려.
- 때문에 Member에서 Address를 참조하는것과 Delivery에서 Address를 참조하는것이 공유 되면서 한쪽을 수정할 경우 다른쪽도 수정이 되어버리는 대참사가 발생할 수도 있어.
- 때문에 이 임베디드 타입 용으로 만든 클래스는 immutable하게 관리 되어야 함.
- 때문에 setter를 만들지 않는다거나, setter를 만들되 접근 연산자를 private으로 두어야 돼.
- setter를 없앤다면, 생성자를 잘 만들어줘야 겠지. 기본 생성자도 하나 있어야 하고, equals와 hashcode 메소드도 만들어줘야함.
- 임베디드 타입 용 클래스쪽에는 @Entity 붙이면 안돼. 왜냐하면 JPA에서 관리하는 엔티티가 아니기에.
- @Embeddable 이라 붙이고, 해당 객체를 참조하는 필드에는 @Embedded 라 붙임.(필드에는 안붙여도 기능하지만, 명시적으로 확인하기 위해 관례상)
- 콜렉션 타입의 값 타입은 값을 갱신하거나, 고유 식별자도 없어서 추척하기에 아주 불편해. 그냥 쓰지 마.


#### 객체지향 쿼리 언어1 - 기본 문법
- "나이가 18세 이상인 회원을 불러와라."
- 와 같은 조건을 걸고 전체를 조회해야 하는 상황에서는 지금까지 했던 em.find() 가지고는 할수가 없어.
- em.find() 는 id 값을 인자로 넣어서 그에 해당되는 하나의 인스턴스를 찍어서 가져오는 것이기에.
- 이러한 상황에서는 결국 쿼리를 날려야 되는데, 순수 쿼리 문장을 작성하면 오타도 문제고 string을 막 자르고 붙이고, 혹여 동적 쿼리를 짠다거나 하면 이러저러한 이유로 불편해.
- 이러한 문제를 JPQL이 해결해줌.
- 실무에서는 JPQL과 QueryDSL로 95%이상 해결하고, 나머지는 SpringJdbcTemplate나 네이티브 SQL로 해결.
- 기본 방법
  - em.createQuary("select m from Member m where m.age >= 18", Member.class).getResultList();
  - from Member 할때 Member는 테이블이 아니라 엔티티야. 즉, 테이블에 날리는 sql이 아니라 객체에게 날리는 쿼리인거야.
- 동적 쿼리 날리는 법
  - query = em.createQuary("select m from Member m where m.username =:username", Member.class);
  - query.setParameter("username", "member1");
  - query.getResultList();
- 위의 예 처럼 타입이 Member로 명확할떄는 createQuary()의 반환값을 TypeQuery<Member> query로 받게 됨. 
- select m.username, m.age from Member m.. 같이 여러값 조회가 필요하거나, 타입이 명확하지 않을때는,
  Query 타입으로 조회, Object[] 타입으로 조회, new 명령어로 조회 하는 방식이 있어.
- Oracle에서는 페이징 쿼리 날리려면 rownum을 가지고 별짓을 다 3 depth 쿼리문을 날려야하는데,
- em.createQuary().setFirstResult(),setMaxResults().getResultList() 하면 각각의 sql 방언에 따라 알아서 잘 날려줌.
- jpql 작성법
  - 일단 기본 대전제는 표준 sql 문법은 그냥 거의 다 똑같아.
  - 몇가지 다른 표현 방식이 있는데
  - 테이블은 무조건 별칭 써야돼.
  - join 문 작성할때.
    - 연관관계가 있는 엔티티일 경우, 그냥 from 절에서 "from Member m join m.team t" 이리 하면 됨.
      즉 원래 sql문에서 써야 했을 join 조건식인 "on m.team_id = t.id" 을 따로 안써줘도 됨.
    - 연관관계가 없을 경우, on 뒤에 조건식 써줘야 되고.
    - 묵시적 조인: select t.member from Team t; 
    - 명시적 조언: select m from Team t join t.members m;
    - 묵시적 조인 실무에서 쓰지마.
  - 서브쿼리도 다 돼. 다만 **from 절에서는 서브쿼리를 쓸 수 없어**
    - 대안으로는 join으로 풀어서 쓰거나, 쿼리문을 각각 두번 날려서 그걸 애플리케이션쪽에서 조합해서 쓰거나, 네이티브 쿼리 써야돼.
  - 상속 관계에 있는 엔티티일 경우 
    - "select i from Item i where type(i) = IN (Book, Movie)" 처럼 타입을 제한하는 조건식도 가능.
    - "select i from Item i where treat(i as Book).auther = 'kim'" 처럼 Book에만 있는 속성에 접근 가능.
  - JPQL 기본 함수 중 size()는 컬렉션에 담긴 사이즈를 확인할 수 있는데, 왠지 안됨.
  - 사용자 정의 함수도.. 일반 보류. 뭔가 H2 DB와 버전 문제로 연동이 잘 안되는 느낌
  - option + enter 해서 inject language 기능으로 적절한 언어를 선택하면, 
    쿼리 문을 초록색 문자열 상태에서 쿼리 문법을 구분할 수 있게 색깔 구분하게 해주는데, enterprise만 JPA QL 선택 가능.


#### 객체지향 쿼리 언어2 - 중급 문법
- 페치 조인
  - 실무에서 자주 쓰이고, 원래 RDB에 있는 join이 아니라 JPA에서 만들어낸 join임.
  - JPA의 70~80% 성능문제는 다 페치 조인으로 잡을만큼 중요!
  - 상황 Member와 Team이 다대일 연관관계.
    - em.createQuary("select m from Member m"); 이후 결과가 List<Member> members에 담김.
    - for(Member member : members) member.getTeam().getName();
    - Member 필드에 있는 team은 지연로딩 설정 해놨기에, 처음에는 프록시로 값을 대체하다가 getName() 할때 진짜 데이터를 DB에서 가져오게됨.
    - 근데 이렇게 되면, for을 돌때마다 1차캐시에 없는 team 데이터일 경우 DB한테 sql를 날려줘야겠지.
    - 이때! 페치 조인이 등장!
    - 처음 쿼리문을 "select m from Member m join fetch m.team" 이라 해버리면, JPA가 member와 team 데이터 다 가져오는 inner join 쿼리문을 달려줌
    - Q. "그러면 애초에 지연 로딩 설정 걸지 말고 즉시 로딩 해놓으면 페치 조인 안해도 되는거 아니야?"
    - A. "기본적으로 즉시 로딩을 지양하라고 하는 이유는 불필요한 부분도 조회하기 때문. 예를 들어 A만 필요해서 조회했는데 즉시 로딩으로 설정해둔 B, C 등도 같이 조회하게 되면 이것은 리소스 낭비.
      lazy loading + fetch join을 권장드리는 이유는 본질적으로 필요할 때만 같이 불러오기 위해서입니다. A만 필요할 때는 A만 부르고 A와 B를 같이 부르고 싶을 때 fetch join을 사용하는 것입니다.
      부차적으로는 즉시 로딩을 남발할 때 예상치 못한 SQL이 작성될 수 있습니다. jpa는 어디까지나 자동으로 SQL을 만들어주기 때문에 원래 의도했던 SQL과는 다르게 SQL이 나갈 수 있습니다."
  - 상황 Team과 Member의 일대다 연관관계
    - select t from Team t; 의 경우 결과를 담은 컬렉션(이하 결과)이
    - {팀A, 팀B}가 나옴.
    - select t from Team t join (fetch) t.members; 의 경우 결과가
    - {팀A : 회원1, 팀A : 회원2, 팀B : 회원3} 으로 나옴
    - 즉 데이터가 조인을 하면서 뻥튀기 되는것.
    - 이때 등장하는게 distinct
    - select distinct t from Team t join fetch t.members;
    - 원래 sql에서 사용하는 distinct의 경우 모든 속성값이 동일해야만 중복을 제거해주는데,
    - JPQL에서 사용하는 distinct의 경우 같은 pk를 가진 엔티티의 중복을 제거해줌.
    - 따라서 결과가 {팀A : 회원1 회원2, 팀B : 회원3} 이런식으로 나옴.
    - team.getMembers().size()가 3에서 2로 줄게됨.
- 페치 조인의 한계
  - 페치 조인한 테이블에 별칭을 찍을 수 없다.
    - 페치 조인은 관련된 데이터를 다 가져오는것이 기본인데, 별칭 찍고 그 별칭으로 where문 작성해서 데이터를 거르고 그러면 이게 안맞아.
  - 둘 이상의 컬렉션은 페치 조인 할 수 없다. 일대다도 데이터 뻥튀기 되는데, 일대다대다.. 이런상태에 페치조인 할 수 없다.
  - 일대다로 페치 조인 했을때, 페이징 api 사용 불가. 데이터 뻥튀기 되는것 때문에. 
    - 그래서 일단 쿼리문에서 join을 그냥 깔끔하게 없애고, @BatchSize(size = 100) 을 @OneToMany 붙인 필드에다가 붙이거나, 
      글로벌하게 persistence.xml에 hibernate.default_batch_fetch_size 값을 100 (보통 1000이하) 으로 줌.
    - 이렇게 하면 설정한 값의 크기만큼 한번에 데이터를 넉넉히 가져오게 되어 N + 1 문제 해결.
- @NamedQuary
  - @Entity 바로 아래에다가 쿼리문을 선언해 놓을 수 있어.
  - 애플리케이션 로딩 시점에 해당 네임드쿼리를 파싱해서 검증하기 때문에, 잘못된 쿼리문이라면 컴파일 단계에서 오류가 뜸.
  - 다만, 현재 표현법이 지저분한데, 이는 Spring Data JPA에서 조금 더 깔끔하게 사용할 수 있어.
- 벌크 연산
  - update, delete, insert 연산을 말함. 
  - em.createQuary("update Member m set m.age = 20").executeUpdate();
  - executeUpdate() 연산이 진행된 엔티티 수를 반환.
  - insert into select.. 문도 다 가능
  - 주의할점은 벌크 연산은 영속성컨텍스트를 통해 진행되지 않고 그냥 바로 DB에 접근함.
  - 때문에, 벌크 연산 수행 후 em.find() 어쩌구 하면, DB에서 새롭게 가져오는 데이터면 상관없는데, 1차캐시에서 꺼내오면 갱신이 반영 안되어 있겠지.
  - 때문에, 벌크 연산 수행 후 에는 em.clear() 해줘.

-------------------------------
