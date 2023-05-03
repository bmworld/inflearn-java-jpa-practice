package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.Team;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {
  
  @Autowired
  EntityManager em;
  
  @Autowired
  MemberJpaRepository memberJpaRepository;
  
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
    memberJpaRepository.save(member);
    
    // Then
    Member foundMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(foundMember).isEqualTo(member);
    
    List<Member> result1 = memberJpaRepository.findAll();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberJpaRepository.findByName(member.getName());
    assertThat(result2).containsExactly(member);
    
  }
  
  @DisplayName("basicQuerydslTest")
  @Test
  public void basicQuerydslTest() throws Exception {
    // Given / When
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);
    
    // Then
    Member foundMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(foundMember).isEqualTo(member);
    
    List<Member> result1 = memberJpaRepository.findAll_querydsl();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberJpaRepository.findByName_querydsl(member.getName());
    assertThat(result2).containsExactly(member);
  }
  
  
  @Test
  void searchByBuilder() {
    MemberSearchCondition cond = new MemberSearchCondition();
    cond.setAgeGoe(5);
    cond.setAgeLoe(10);
    cond.setTeamName("teamA");
    cond.setName("member-A-1");
    
    
    List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(cond);
    System.out.println("result = " + result);
    assertThat(result).extracting("name")
        .containsExactly("member-A-1");
    
  }
  
  
  @Test
  void searchByWhereParam() {
    MemberSearchCondition cond = new MemberSearchCondition();
    cond.setAgeGoe(5);
    cond.setAgeLoe(10);
    cond.setTeamName("teamA");
    cond.setName("member-A-1");
    
    
    List<MemberTeamDto> result = memberJpaRepository.searchMemberTeamDtoByWhereCond(cond);
    System.out.println("result = " + result);
    assertThat(result).extracting("name")
        .containsExactly("member-A-1");
    
  }



// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
// #################################################################
  
  private void createMember(String name, int age) {
    Member m = new Member(name, age);
    memberJpaRepository.save(m);
  }
  
  private void createMember(String name, int age, Team team) {
    Member m = new Member(name, age, team);
    memberJpaRepository.save(m);
  }
  
  private void createMembers(List<String> names) {
    for (String name : names) {
      int order = names.indexOf(name) + 1;
      Member m = new Member(name, 10 * order);
      memberJpaRepository.save(m);
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
      memberJpaRepository.save(m);
    }
  }
  
  
  
}
