package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;
import study.querydsl.domain.Team;
import study.querydsl.dto.AnotherMemberDto;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.MemberWithQueryProjectionDto;
import study.querydsl.dto.QMemberWithQueryProjectionDto;
import study.querydsl.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.domain.QMember.*;
import static study.querydsl.domain.QTeam.team;

@SpringBootTest
@Rollback(false)
@Transactional
public class QuerydslBasicTest {
  
  
  @PersistenceUnit
  EntityManagerFactory emf;
  
  
  @PersistenceContext
  EntityManager em;
  
  @Autowired
  MemberRepository memberRepository;
  
  JPAQueryFactory queryFactory; // QueryDSL 은 JPQL Builder 역할
  
  
  /**
   * <pre>
   * Spring 에서 주입하는 Entity Manager는 multy-thread에 문제없이 주입되도록 처리가 되어있다.
   * -> Trasaction 따라서 자동 Biding 처리가 되어있음</pre>
   */
  @BeforeEach
  private void beforeInit() {
    
    queryFactory = new JPAQueryFactory(em); // 동시성 문제 없이 Field에 할당해도 무방함.
    
    
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);
    
    createMembers(getMemberNameList(2, "A"), teamA);
    createMembers(getMemberNameList(2, "B"), teamB);
    
    
    System.out.println("----------- beforeInit: DONE");
  }
  
  
  /**
   * <h2>QClass 사용방법</h2>
   * <pre>
   * <h3>1. 별칭 직접 지정</h3>
   * : 같은 Table 을 JOIN 하는 경우에만 사용하시라.</pre>
   *
   * <pre>
   * <h3>2., 3. 기본 QClass Instance 사용 [강사님 권장]</h3>
   * : 기본 QClass를 사용 / static Import</pre>
   */
  @Test
  @DisplayName("Q파일 사용 방법")
  void Q파일사용() {
    
    // 1. 별칭 직접 지정
    QMember m1 = new QMember("m");
    Member member1 = queryFactory
        .selectFrom(m1)
        .where(m1.name.eq("member1"))
        .fetchOne();
    
    // 2. 기본 인스턴스 사용
    QMember m2 = QMember.member;
    Member member2 = queryFactory
        .selectFrom(m2)
        .where(m2.name.eq("member1"))
        .fetchOne();
    
    // 3. 직접 static import 하여 사용 -> import static kbds.querydsl.domain.QMember.member;
    Member member3 = queryFactory
        .selectFrom(member)
        .where(member.name.eq("member1"))
        .fetchOne();
    
    assertThat(member1.getAge()).isEqualTo(member2.getAge()).isEqualTo(member3.getAge());
  }
  
  
  @DisplayName("startJPQL")
  @Test
  public void testJPQL() throws Exception {
    // Given
    String targetMemberName = "member-A-1";
    Member foundMember = em.createQuery("select m from Member m where m.name =:name", Member.class)
        .setParameter("name", targetMemberName)
        .getSingleResult();
    // When
    
    // Then
    assertThat(foundMember.getName()).isEqualTo(targetMemberName);
    
  }
  
  
  /**
   * <h2>QueryDSL</h2>
   * <pre>
   *   QueryDSL은 prepareStatment를 사용하여
   *   Parameter Binding 하지 않더라도,
   *   eq() 메서드  등이 추가되어있을 경우,
   *   자동으로 parameter Binding을 추가해준다.
   *  </pre>
   */
  @DisplayName("startQuerydsl")
  @Test
  public void testQuerlDSL() throws Exception {
    // Given
    String targetMemberName = "member-A-1";
    
    // When
    Member foundMember = queryFactory
        .select(member)
        .from(member)
        .where(member.name.eq(targetMemberName)) // Parameter Binding 처리
        .fetchOne();
    // Then
    
    assertThat(foundMember.getName()).isEqualTo(targetMemberName);
  }
  
  
  /**
   * <h2>QueryDSL은 JPQL이 제공하는 모든 검색 조건을 제공</h2>
   * <pre>
   *   member.username.eq("member1") // username = 'member1'
   *   member.username.ne("member1") //username != 'member1'
   *   member.username.eq("member1").not() // username != 'member1'
   *   member.username.isNotNull() //이름이 is not null
   *   member.age.in(10, 20) // age in (10,20)
   *   member.age.notIn(10, 20) // age not in (10, 20)
   *   member.age.between(10,30) //between 10, 30
   *   member.age.goe(30) // age >= 30
   *   member.age.gt(30) // age > 30
   *   member.age.loe(30) // age <= 30
   *   member.age.lt(30) // age < 30
   *   member.username.like("member%") like 검색
   *   member.username.contains("member") // like ‘%member%’ 검색
   *   member.username.startsWith("member") //like ‘member%’ 검색
   *   ...
   * </pre>
   */
  @DisplayName("search: QueryDSL은 JPQL이 제공하는 모든 검색 조건을 제공")
  @Test
  public void search() throws Exception {
    // Given/When
    String targetName = "member-A-1";
    Member foundMember = queryFactory
        .selectFrom(member)
        .where(
            member.name.eq(targetName)
                .and(member.age.between(10, 40))
        )
        .fetchOne();
    
    // Then
    assertThat(foundMember.getName()).isEqualTo(targetName);
    
  }
  
  
  /**
   * <h2> [ 권장 ] Multi Search Param: Where 절 내에서 동적쿼리 & AND절 조립방법</h2>
   * <pre>
   *   - Where 절 내에서 쉼표(,)를 사용하면 AND 절로 조립된다.
   *   - Null 무시됨 => 동적 Query 작성시 매우 유용</pre>
   */
  
  @DisplayName("Multi Search Param: Where 절 내에서, 쉼표(,)를 사용하면 AND절로 조립된다. ")
  @Test
  public void searchAndParam() throws Exception {
    // Given/When
    String targetName = "member-A-1";
    Member foundMember = queryFactory
        .selectFrom(member)
        .where(
            member.name.eq(targetName),
            member.age.between(10, 40)
        )
        .fetchOne();
    
    // Then
    assertThat(foundMember.getName()).isEqualTo(targetName);
  }
  
  
  /**
   * <h2>결과조회</h2>
   * <pre>
   *   - fetch(): 리스트 조회, 데이터 없을 경우, 빈 리스트 [] 반환
   *
   *   - fetchOne(): 단 건 조회
   *      - 결과 없을 경우: null
   *      - 결과 둘 이상: `com.querydsl.core.NonUniqueResultException`
   *
   *   - fetchFirst() => limit(1).fetchOne()
   *
   *   - (*주의) fetchResults(): 페이징 정보 포함, total count query 추가 실행
   *     => 성능이 중요할 경우, count Query와 content 쿼리 분리하시라.
   *
   *   - fetchCount(): count Query로 변경하여 count 수 조회
   * </pre>
   */
  @DisplayName("resultFetch")
  @Test
  public void resultFetch() throws Exception {
    // 리스트 조회
    List<Member> fetchList = queryFactory
        .selectFrom(member)
        .fetch();
    
    // 단건 조회
    Member fetchOne = queryFactory
        .selectFrom(member)
        .where(member.name.eq("member-A-1"))
        .fetchOne();
    
    // 리스트 중, 첫 번째 결과 조회
    Member fetchFirst = queryFactory
        .selectFrom(member)
        .fetchFirst();
    
    
    // 결과조회 + Paging
    QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();
    
    long total = results.getTotal();
    List<Member> content = results.getResults();
    System.out.println("----- content = " + content);
    
    
    // Count 전용 Query
    long totalCount = queryFactory.selectFrom(member).fetchCount();
    
    
  }
  
  
  /**
   * <h2>정렬기능</h2>
   * <pre>
   *   Example. 회원 정렬 순서
   *   1. 회원 나이 내림차순 (desc)
   *   2. 회원 나이 올림차순 (asc)
   *   - 단, 2 조건에서 회원 이름이 없을 경우, 마지막에 출력
   *
   * </pre>
   */
  @DisplayName("sort")
  @Test
  public void sort() throws Exception {
    // Given
    int targetAge = 100;
    String m1Name = "m1";
    String m2Name = "m2";
    createMember(m1Name, targetAge);
    createMember(m2Name, targetAge);
    createMember(null, targetAge);
    
    
    // When
    List<Member> result = queryFactory
        .select(member)
        .from(member)
        .where(member.age.eq(targetAge))
        .orderBy(member.age.desc(), member.name.asc().nullsLast()) // Null 데이터는 맨 마지막으로
        .fetch();
    
    
    // Then
    Member member1 = result.get(0);
    Member member2 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member1.getName()).isEqualTo(m1Name);
    assertThat(member2.getName()).isEqualTo(m2Name);
    assertThat(memberNull.getName()).isNull();
    
  }
  
  
  @DisplayName("paging1")
  @Test
  public void paging1() throws Exception {
    // Given / When
    int limit = 3;
    List<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.name.desc())
        .offset(1)
        .limit(limit)
        .fetch();
    
    // Then
    assertThat(result.size()).isEqualTo(limit);
  }
  
  
  @DisplayName("paging2")
  @Test
  public void paging2() throws Exception {
    // Given / When
    int limit = 3;
    int offset = 1;
    QueryResults<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.name.desc())
        .offset(1)
        .limit(limit)
        .fetchResults();
    
    // Then
    assertThat(result.getTotal()).isEqualTo(4);
    assertThat(result.getLimit()).isEqualTo(limit);
    assertThat(result.getOffset()).isEqualTo(offset);
    assertThat(result.getResults().size()).isEqualTo(limit);
  }
  
  
  /**
   * <h2> - 집합 구하기</h2>
   * <pre>
   *
   * </pre>
   *
   * <br>
   *
   * <h2> - Tuple</h2>
   * <pre>
   *   다중 컬럼 결과를 받기 위한 Type
   *
   * </pre>
   */
  @DisplayName("aggregation: 집합 구하기")
  @Test
  public void aggregation() throws Exception {
    // Given
    List<Tuple> result = queryFactory
        .select(
            member.count(),
            member.age.sum(),
            member.age.avg(),
            member.age.max(),
            member.age.min()
        )
        .from(member)
        .fetch();
    
    // When
    Tuple tuple = result.get(0);
    // Then
    System.out.println("tuple.get(member) = " + tuple.get(member));
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(60);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
    assertThat(tuple.get(member.age.max())).isEqualTo(20);
    
  }
  
  @DisplayName("group: 팀 이름과 각 팀의 평균 연령구하기 + having")
  @Test
  public void group() throws Exception {
    // Given
    List<Tuple> result = queryFactory
        .select(team.name, member.age.avg())
        .from(member)
        .join(member.team, team)
        .groupBy(team.name)
        .having(team.name.contains("A").or(team.name.contains("B"))) // groupBy 의 해당하는 column 내부에 존재하는 값에 대해서만 having절 적용할 수 있음
        .fetch();
    
    // When
    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);
    
    
    // Then
    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);
    
    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(15);
    
    
  }
  
  
  @DisplayName("join")
  @Test
  public void join() throws Exception {
    // Given / When
    List<Member> result = queryFactory
        .selectFrom(member)
        .join(member.team, team)
        .where(team.name.eq("teamA"))
        .fetch();
    
    
    // Then
    assertThat(result)
        .extracting("name")
        .containsExactly("member-A-1", "member-A-2");
    
  }
  
  
  /**
   * <h2>THETA JOIN : 연관관계까 없는 Field를 사용한 Join</h2>
   * <pre>
   *   - From 절에 여러 Entity를 선택하여 THETA JOIN
   *   - 연관관계가 없는 Table을 Join하여, 조건에 맞는 신규 Table Rows 반환.
   *   - 외부 조인 불가능
   *     => BUT, 최근 queryDsl 버전이 올라가면서 JOIN ON을 사용하여 가능해짐
   * </pre>
   *
   * <h3>주의사항</h3>
   * <pre>
   *   1. Join Condition: 데이터 타입이 불일치 할 경우, 올바르지 않은 결과를 Return할 수 있음
   *   2. Table Size: THETA JOIN은 비싼 연산이다.
   *   3. Table Structure: 조인 중, Data type 불일치 시, Data 증발할 수 있다.
   *   4. Duplicate Data: 만약, Join된 두 Table에 대하여, 조건이 일치할 경우, 중복된 rows가 생성된다.
   *   5. Performance: 데이터 조회에 들어가는 비싼 비용만큼, 최적화에 신경써야한다.
   * </pre>
   */
  @DisplayName("THETA JOIN: 연관관계가 없는 Table을 Join 가능")
  @Test
  public void theta_join() throws Exception {
    // Given
    Team t = new Team("t-C");
    em.persist(t);
    em.persist(new Member("m-A", 11, t));
    em.persist(new Member("m-B", 22, t));
    em.persist(new Member("m-C", 33, t));
    
    // When
    List<Member> result = queryFactory
        .select(member)
        .from(member, team)
        .where(member.name.contains("C"), team.name.contains("C"))
        .fetch();
    
    
    // Then
    assertThat(result)
        .extracting("name")
        .containsExactly("m-C");
    
  }
  
  
  /**
   * <h2>JOIN ON절 </h2>
   * <pre>
   *   - 용도
   *   1. JOIN 대상 filtering
   *   2. 연관관계 없는 Entity OUTER JOIN
   *
   *
   *   참고)
   *   - INNER JOIN => WHERE 절 사용
   *      (INNER JOI => ON 절과  WHERE 절의 결과는 동일함)
   *   - LEFT/RIGHT OUTER JOIN => ON 절 사용
   *      (WHERE 절로 해결할 수 없음)
   *
   *
   *   결론)
   *    ON 절은정말 OUTER JOIN 필요 시에만 사용하시라.
   * </pre>
   * <pre>
   *   Example: 회원과 팀을 조인하되, 팀 이름이 teamA인 Team만 JOIN & 회원은 모두 조회
   *   - JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
   * </pre>
   */
  @DisplayName("join_ON")
  @Test
  public void join_with_on_filtering() throws Exception {
    // Given
    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(member.team, team).on(team.name.eq("teamA"))
        .fetch();
    
    // When
    
    // Then
    for (Tuple tuple : result) {
      // leftJoin 시, team값 조건이 맞지 않을 경우, null 값 출력됨.
      System.out.println("----- tuple = " + tuple);
      
    }
    
  }
  
  
  @DisplayName("연관관계 없는 Entity의 OUTER JOIN")
  @Test
  public void join_with_on_no_relationEntity() throws Exception {
    // Given
    Team t = new Team("t-C");
    em.persist(t);
    em.persist(new Member("m-A", 11, t));
    em.persist(new Member("m-B", 22, t));
    em.persist(new Member("m-C", 33, t));
    
    // When
    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        // 중요) member, team 사이에 연관 관계가 없는 조건이므로, FK 매칭하지 않음 => leftJoin 시, leftJoin(member.team, team) 사용 X
        .join(team).on(member.name.contains("C"), team.name.contains("C"))
        .fetch();
    
    for (Tuple tuple : result) {
      System.out.println("----- tuple = " + tuple);
      
    }
    
    
  }
  
  
  @DisplayName("withoutFetchJoin")
  @Test
  public void withoutFetchJoin() throws Exception {
    // Given
    em.flush();
    em.clear();
    
    // Then
    Member foundMember = queryFactory
        .selectFrom(member)
        .where(member.name.eq("member-A-1"))
        .fetchOne();
    
    boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(foundMember.getTeam());
    
    assertThat(isLoaded).as("Fetch Join 미적용").isFalse();
  }
  
  
  @DisplayName("withFetchJoin")
  @Test
  public void withFetchJoin() throws Exception {
    // Given
    em.flush();
    em.clear();
    
    // Then
    Member foundMember = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin() // fetchJoin 사용
        .where(member.name.eq("member-A-1"))
        .fetchOne();
    
    boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(foundMember.getTeam());
    
    assertThat(isLoaded).as("Fetch Join 적용").isTrue();
  }
  
  
  /**
   * <h3>SubQuery</h3>
   * <pre>
   *   - Alias 중복피하기위해, SubQuery Depth에 맞게 QClass 생성.
   *   - `SELECT 절`, `WHERE 절` 사용 가능.
   *   - 한계: `FROM 절`에서 subQuery 지원하지 않음.
   *     => WHY ? JPQL에서 지원하지 않음
   *     => queryDSL은, JPQL BUILDER역할을 할 뿐이므로, JPQL에서 지원하지 않으면, 별 수 없다.
   *
   *
   *    * `FROM 절` SubQuery 미지원에 대한 해결책
   *     1. SubQuery를 JOIN 으로 변경 ( 불가능한 상황도 있다.)
   *     2. App에서 Query를 (2회) 분리하여 실행
   *     3. native SQL 사용
   * </pre>
   */
  @DisplayName("subQuery - ex) 나이가 가장 많은 회원 조회")
  @Test
  public void subQuery() throws Exception {
    
    // Given / When
    QMember memberSub = new QMember("memberSub");// Alias 중복을 피하기 위해 생성
    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(
            JPAExpressions
                .select(memberSub.age.max())
                .from(memberSub)
        ), member.team.name.eq("teamA"))
        .fetch();
    
    // Then
    assertThat(result)
        .extracting("age")
        .containsExactly(20);
  }
  
  
  @DisplayName("subQuery - Greater or Equal: 나이가 평균 이상인 회원")
  @Test
  public void subQueryGoe() throws Exception {
    
    // Given / When
    QMember memberSub = new QMember("memberSub");// Alias 중복을 피하기 위해 생성
    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.goe(
            JPAExpressions.select(memberSub.age.avg())
                .from(memberSub)
        ), member.team.name.eq("teamA"))
        .fetch();
    
    // Then
    System.out.println("result = " + result);
    assertThat(result)
        .extracting("age")
        .containsExactly(20);
    
  }
  
  
  @DisplayName("subQuery - IN절: 특정 나이인 회원")
  @Test
  public void subQueryIn() throws Exception {
    
    // Given / When
    QMember memberSub = new QMember("memberSub");// Alias 중복을 피하기 위해 생성
    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.in(
            JPAExpressions
                .select(memberSub.age)
                .from(memberSub)
                .where(memberSub.age.gt(7))
        ), member.team.name.eq("teamA"))
        .fetch();
    
    // Then
    System.out.println("result = " + result);
    assertThat(result)
        .extracting("age")
        .containsExactly(10, 20);
  }
  
  
  @DisplayName("subQueryInSelect절")
  @Test
  public void subQueryInSelect절() throws Exception {
    // Given / When
    QMember memberSub = new QMember("memberSub");// Alias 중복을 피하기 위해 생성
    List<Tuple> result = queryFactory
        .select(
            member.name,
            JPAExpressions
                .select(memberSub.age.avg())
                .from(memberSub)
        )
        .from(member)
        .fetch();
    
    // Then
    
    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }
  
  
  /**
   * <pre>
   *   CASE문은 가급적 사용하지 마시라.
   *   Presentation Logic에서 해결가능하다면, 더더욱.</pre>
   */
  @DisplayName("`CASE` 문 - 기본 CASE")
  @Test
  public void basicCase() throws Exception {
    // Given
    List<String> result = queryFactory
        .select(member.age
            .when(10).then("열살")
            .when(20).then("스무살")
            .otherwise("기타")
        )
        .from(member)
        .fetch();
    
    // Then
    for (String s : result) {
      System.out.println("----- s = " + s);
    }
    
  }
  
  
  @DisplayName("`CASE` 문 - 복잡한 CASE")
  @Test
  public void complexCase() throws Exception {
    // Given
    List<String> result = queryFactory
        .select(
            new CaseBuilder()
                .when(member.age.between(0, 10)).then("0~열 살")
                .when(member.age.between(0, 220)).then("0~스무 살")
                .otherwise("기타")
        )
        .from(member)
        .fetch();
    
    // Then
    for (String s : result) {
      System.out.println("----- s = " + s);
    }
    
  }
  
  
  /**
   * <h2>CONCAT: 문자 더하기 </h2>
   * <pre>
   *   `stringValue()`: 문자가 아닌 타입들을 문자로 변환가능 (Int, ENUM 등)</pre>
   */
  @DisplayName("constant")
  @Test
  public void constant() throws Exception {
    // Given
    List<Tuple> result = queryFactory
        .select(member.name, Expressions.constant("A"))
        .from(member)
        .fetch();
    // Then
    for (Tuple tuple : result) {
      System.out.println("----- tuple = " + tuple);
    }
  }
  
  
  @DisplayName("concat")
  @Test
  public void concat() throws Exception {
    // Given
    List<String> result = queryFactory
        .select(member.name.concat("_").concat(member.age.stringValue()))
        .from(member)
        .where(member.name.eq("member-A-1"))
        .fetch();
    
    
    for (String s : result) {
      System.out.println("----- s = " + s);
    }
    // When
    
    // Then
    
  }
  
  
  @DisplayName("simpleProjection")
  @Test
  public void simpleProjection() throws Exception {
    // Given
    List<String> result = queryFactory
        .select(member.name)
        .from(member)
        .fetch();
    // Then
    for (String m : result) {
      System.out.println("----- m = " + m);
    }
    
  }
  
  @DisplayName("tupleProjection")
  @Test
  public void tupleProjection() throws Exception {
    // Given
    // When
    List<Tuple> result = queryFactory
        .select(member.name, member.age)
        .from(member)
        .fetch();
    
    // Then
    
    for (Tuple tuple : result) {
      String name = tuple.get(member.name);
      int age = tuple.get(member.age);
      
      System.out.println("----- tuple = " + tuple);
      System.out.println("------ name = " + name);
      System.out.println("------ age = " + age);
    }
  }
  
  
  /**
   * <h2>순수 JPQL x DTO 조회</h2>
   * <pre>
   *   - new 명령어 사용해야함
   *   - DTO의 Package 경로 및 이름을 다 명시해줘야함 (지저분)
   *   - DTO의 생성자 방식만 지원</pre>
   */
  @DisplayName("findDtoByJPQL")
  @Test
  public void findDtoByJPQL() throws Exception {
    // Given / When
    List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.name, m.age) from Member m", MemberDto.class)
        .getResultList();
    // Then
    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }
  
  
  /**
   * <h2>Query DSL x DTO 조회 - SETTER </h2>
   * <pre>
   *   - 사용조건 : DTO에 기본생성자(NoArgsConstructor)
   *     => QueryDSL이 기본생성자로 DTO를 먼저 만들고 난 뒤, 값 세팅
   *   - 사용조건 2 : DTO 필드의 Getter, Setter</pre>
   */
  @DisplayName("findDtoBySetterInQueryDSL")
  @Test
  public void findDtoBySetterInQueryDSL() throws Exception {
    // Given
    List<MemberDto> result = queryFactory
        .select(Projections.bean(MemberDto.class,
                member.name,
                member.age
            )
        )
        .from(member)
        .fetch();
    // Then
    for (MemberDto memberDto : result) {
      System.out.println("----- find Dto by setter in querydsl ==>  memberDto = " + memberDto);
    }
    
  }
  
  
  
  /**
   * <h2>Query DSL x DTO 조회 - FIELD </h2>
   * <pre>
   *   - 사용조건 : DTO에 기본생성자(NoArgsConstructor)
   *     => QueryDSL이 기본생성자로 DTO를 먼저 만들고 난 뒤, 값 세팅
   *   - Getter, Setter 없어도 됨.
   * </pre>
   */
  @DisplayName("findDtoByFieldInQueryDSL")
  @Test
  public void findDtoByFieldInQueryDSL() throws Exception {
    // Given
    List<MemberDto> result = queryFactory
        .select(Projections.fields(MemberDto.class,
                member.name,
                member.age
            )
        )
        .from(member)
        .fetch();
    // Then
    for (MemberDto memberDto : result) {
      System.out.println("----- find Dto by FIELD in querydsl ==>  memberDto = " + memberDto);
    }
  }
  
  
  /**
   * <h2>Query DSL x 별칭 (alias)</h2>
   * <pre>
   * - Field & column 이름 다를 경우에 대응할 수 있다.
   * - 필드명 다를 경우, 값을 찾지 못함 (null 할당됨)
   * - `SubQuery`는 `ExpressionUtils` 사용해야함. (그 외 별 수 없음.)
   * </pre>
   */
  @DisplayName("findDtoByFieldInQueryDSAndAlias")
  @Test
  public void findDtoByFieldInQueryDSAndAlias() throws Exception {
    // Given
    QMember memberSub = new QMember("memberSub");
    List<AnotherMemberDto> result = queryFactory
        .select(Projections.fields(AnotherMemberDto.class,
                member.name.as("username"),
                ExpressionUtils.as(JPAExpressions
                .select(memberSub.age.max())
                .from(memberSub), "age"
                )
            )
        )
        .from(member)
        .fetch();
    // Then
    for (AnotherMemberDto memberDto : result) {
      System.out.println("----- find Dto by FIELD in querydsl ==>  memberDto = " + memberDto);
    }
  }
  
  
  
  
  
  /**
   * <h2>Query DSL x DTO 조회 - CONSTRUCTOR </h2>
   * <pre>
   *   - 사용조건 : DTO에 기본생성자(NoArgsConstructor)
   *     => QueryDSL이 기본생성자로 DTO를 먼저 만들고 난 뒤, 값 세팅
   *   - Getter, Setter 없어도 됨.
   * </pre>
   */
  @DisplayName("findDtoByConstructorInQueryDSL")
  @Test
  public void findDtoByConstructorInQueryDSL() throws Exception {
    // Given
    List<MemberDto> result = queryFactory
        .select(Projections.constructor(MemberDto.class,
                member.name,
                member.age
            )
        )
        .from(member)
        .fetch();
    // Then
    for (MemberDto memberDto : result) {
      System.out.println("----- find Dto by CONSTRUCTOR in querydsl ==>  memberDto = " + memberDto);
    }
  }
  
  
  @DisplayName("findDtoByConstructorInQueryDSL2")
  @Test
  public void findDtoByConstructorInQueryDSL2() throws Exception {
    // Given
    List<AnotherMemberDto> result = queryFactory
        .select(Projections.constructor(AnotherMemberDto.class,
                member.name,
                member.age
            )
        )
        .from(member)
        .fetch();
    // Then
    for (AnotherMemberDto memberDto : result) {
      System.out.println("----- find Dto by CONSTRUCTOR in querydsl 2 ==>  memberDto = " + memberDto);
    }
  }
  
  
  /**
   * <h2>QueryDSL x DTO 조회 - QueryProjection</h2>
   * <pre>
   *   - 정의: DTO를 QClass로 만든다.
   *
   *   - 사용조건1 : @QueryProjection Annotation을 DTO의 Constructor에 붙인다.
   *   - 사용조건2 : gradle > compileQuerydsl 다시 생성한다.
   *
   *   - 장점: 필드명 오류를 Compile 시점에 잡을 수 있음
   *          parameter, type Check 가능.
   *
   *   - 단점: DTO는 여러 레이어에서 사용되기 쉽다
   *          그러나 의존성을 가진 상황에서  QueryDSL을 사용하지 않게되는 등의 변동 사항이 발생하면
   *          여러 레이어(Repository, controller, Service)에 영향을 끼치게 된다.
   *         => 따라서, Architecture 구성 시, 이런 점을 숙지하고 진행해야 한다.
   *
   *         => DTO는 의존성 없이 순수 DTO로 쓰는 것이 좋다.
   * </pre>
   */
  @DisplayName("findDtoByQueryProjection")
  @Test
  public void findDtoByQueryProjection() throws Exception{
    // Given
    List<MemberWithQueryProjectionDto> result = queryFactory
        .select(new QMemberWithQueryProjectionDto(member.name, member.age))
        .from(member)
        .fetch();
    // When
    
    // Then
    for (MemberWithQueryProjectionDto memberWithQueryProjectionDto : result) {
      System.out.println("---- memberWithQueryProjectionDto = " + memberWithQueryProjectionDto);
    }
  
  }
  
  
  
  
// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
  
  private void createMember(String name, int age) {
    Member m = new Member(name, age);
    memberRepository.save(m);
  }
  
  private void createMember(String name, int age, Team team) {
    Member m = new Member(name, age, team);
    memberRepository.save(m);
  }
  
  private void createMembers(List<String> names) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order);
      memberRepository.save(m);
    }
  }
  
  private static List<String> getMemberNameList(int memberTotalCount, String suffix) {
    List<String> memberNameList = new ArrayList<>();
    for (int i = 0; i < memberTotalCount; i++) {
      int order = i + 1;
      memberNameList.add("member" + "-" + suffix + "-" + order);
    }
    return memberNameList;
  }
  
  private void createMembers(List<String> names, Team team) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order, team);
      memberRepository.save(m);
    }
  }
  
}
