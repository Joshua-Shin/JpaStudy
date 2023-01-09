## SpringBoot와 JPA를 활용한 웹 애플리케이션 개발 프로젝트
- 본 프로젝트는 <자바 ORM 표준 JPA 프로그래밍 : 기본편> 강의를 수강하며 진행한 실습 프로젝트 입니다.
- 사용 도구 : Java 11, JPA, IntelliJ, H2 database(1.4.200)
-------
### 메모
- Persistence가 설정 조회해서 H2 데이터베이스와 연동된 EntityManagerFactory를 생성
- EntityManagerFactory가 EntityManager를 생성
- 마치 자바 컬렉션 프레임 워크 다루듯이 데이터를 저장, 조회, 갱신, 삭제 할 수 있음
- .persist(), .remove(), .find()
- find 해서 받은 인스턴스.setName();
- 전체 검색 하고 그럴때는 JPQL 사용.
- 일종의 객제 지향 SQL 임.

