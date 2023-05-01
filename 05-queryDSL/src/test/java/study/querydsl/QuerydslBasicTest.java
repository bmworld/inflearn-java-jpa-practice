package study.querydsl;

import com.querydsl.core.QueryResults;
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
import study.querydsl.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static study.querydsl.domain.QMember.*;

@SpringBootTest
@Rollback(false)
@Transactional
public class QuerydslBasicTest {

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

// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################


  private static List<String> getMemberNameList(int memberTotalCount, String suffix) {
    List<String> memberNameList = new ArrayList<>();
    for (int i = 0; i < memberTotalCount; i++) {
      int order = i + 1;
      memberNameList.add("member" + "-" + suffix + "-" + order);
    }
    return memberNameList;
  }


  private void createMembers(List<String> names) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order);
      memberRepository.save(m);
    }
  }

  private void createMembers(List<String> names, Team team) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order, team);
      memberRepository.save(m);
    }
  }

}
