package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;
import study.querydsl.domain.Team;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
  
  
  @Autowired
  EntityManager em;
  
  @Autowired
  MemberRepository memberRepository;
  
  @Autowired
  JPAQueryFactory queryFactory;
  
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
  
  @DisplayName("basicTest")
  @Test
  public void basicTest() throws Exception {
    // Given / When
    Member member = new Member("member1", 10);
    memberRepository.save(member);
    
    // Then
    Member foundMember = memberRepository.findById(member.getId()).get();
    assertThat(foundMember).isEqualTo(member);
    
    List<Member> result1 = memberRepository.findAll();
    assertThat(result1).contains(member);
    
    List<Member> result2 = memberRepository.findByName(member.getName());
    assertThat(result2).contains(member);
    
  }
  
  
  
  @Test
  void searchByWhereParam() {
    MemberSearchCondition cond = new MemberSearchCondition();
    cond.setAgeGoe(5);
    cond.setAgeLoe(10);
    cond.setTeamName("teamA");
    cond.setName("member-A-1");
    
    List<MemberTeamDto> result = memberRepository.searchMemberTeamDtoByWhereCond(cond);
    assertThat(result).extracting("name")
        .containsExactly("member-A-1");
    
  }
  
  @DisplayName("searchPageSimple")
  @Test
  public void searchPageSimple() throws Exception{
    // Given
    int totalContentCount = 3;
    String searchKeywordOfName = "member-A";
    MemberSearchCondition cond = new MemberSearchCondition();
    cond.setName(searchKeywordOfName);
    
    PageRequest pageRequest = PageRequest.of(0, totalContentCount);
    
    // When
    Page<MemberTeamDto> result = memberRepository.searchPageSimple(cond, pageRequest);
    System.out.println("result = " + result.getContent());
    System.out.println("result.getSize = " + result.getSize());
    // Then
    assertThat(result.getSize()).isEqualTo(totalContentCount);
    assertThat(result.getContent()).extracting("name")
        .containsExactly(searchKeywordOfName+"-1", searchKeywordOfName+"-2");
    
  }
  
  
  
  @DisplayName("searchPageComplex")
  @Test
  public void searchPageComplex() throws Exception{
    // Given
    int totalContentCount = 3;
    String searchKeywordOfName = "member-A";
    MemberSearchCondition cond = new MemberSearchCondition();
    cond.setName(searchKeywordOfName);
    
    PageRequest pageRequest = PageRequest.of(0, totalContentCount);
    
    // When
    Page<MemberTeamDto> result = memberRepository.searchPageComplex(cond, pageRequest);
    System.out.println("result = " + result.getContent());
    System.out.println("result.getSize = " + result.getSize());
    // Then
    assertThat(result.getSize()).isEqualTo(totalContentCount);
    assertThat(result.getContent()).extracting("name")
        .containsExactly(searchKeywordOfName+"-1", searchKeywordOfName+"-2");
    
  }
  
  
  /**
   * <h3>QueryDSL 에서 제공하는 조건문 Query</h3>
   * <pre>
   *   - 복잡한 실무환경에서 사용하기 부적합
   *   - 이유1. JOIN 불가 (묵시적 조인은 가능)
   *   - 이유2. Client가 QueryDSL에 의존해야함 -> 서비스 class가 Querydsl이라는 구현에 의존해야함.
   * </pre>
   */
  @DisplayName("querydslExecutorTest")
  @Test
  public void querydslExecutorTest() throws Exception{
    // Given
    QMember member = QMember.member;
    Iterable<Member> result = memberRepository.findAll(member.age.between(20, 30).and(member.name.eq("member-A-2")));
    for (Member m : result) {
      System.out.println("----- m = " + m);
    }
    // When
    
    // Then
  
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
